import json
import os
import psycopg2
import random
from heapq import heappop, heappush
from typing import Optional, List
from fastapi import FastAPI, Query

app = FastAPI()

#===================================================================================

# For Heroku:

# Initialize DB connection:
# HOST = os.environ['HOST']
# DATABASE = os.environ['DATABASE']
# USER = os.environ['USER']
# PASSWORD = os.environ['PASSWORD']
# PORT = os.environ['PORT']

# For Local execution:

HOST = ''
DATABASE = ''
USER = ''
PASSWORD = ''
PORT = ''

with open('config.json') as data:
    configuration = json.load(data)
    HOST = configuration['HOST']
    DATABASE = configuration['DATABASE']
    USER = configuration['USER']
    PASSWORD = configuration['PASSWORD']
    PORT = configuration['PORT']

conn = psycopg2.connect( 
    host=HOST,
    database=DATABASE,
    user=USER,
    password=PASSWORD,
    port=PORT)

cur = conn.cursor()
#===================================================================================

# Helper functions:

def get_all_recipes_with_iid(iid):
    command = f"select * from recipes r where recipe_id = ANY(SELECT rid FROM recipe_ingredients ri WHERE ri.iid = {iid})"
    cur.execute(command)
    return cur.fetchall()

def get_priority(uid,iid):
    command = f"select avg(fitness) from habits where user_id={uid} and ingredient_id ={iid}"
    cur.execute(command)
    res = cur.fetchone()[0]

    if res == None:
        return 0
    else:
        return float(res)

#===================================================================================

# Routes:

# Throw Grocery item (Fridge)
@app.get('/fridge/throw/{uid}/{iid}')
async def throw_item(uid:int, iid:int, q: Optional[str] = None, short: bool = False):
    command = f"UPDATE fridge SET quantity = 0 WHERE user_id = {uid} and ingredient_id = {iid}"
    cur.execute(command)
    conn.commit()

# Add Grocery Item (Fridge)
@app.get('/fridge/add/{uid}/{iid}')
async def add_item(uid:int, iid:int, q: Optional[str] = None, short: bool = False):
    command = f"UPDATE fridge SET quantity = quantity + 1 WHERE user_id = {uid} and ingredient_id = {iid}"
    cur.execute(command)
    conn.commit()

    return 1

# Remove Grocery Item (Fridge)
@app.get('/fridge/remove/{uid}/{iid}')
async def remove_item(uid:int, iid:int, q: Optional[str] = None, short: bool = False):
    command = f"SELECT quantity FROM fridge WHERE user_id = {uid} and ingredient_id = {iid}"
    cur.execute(command)
    quantity = cur.fetchone()[0]

    if quantity == 0:
        return 1

    command = f"UPDATE fridge SET quantity = quantity - 1 WHERE user_id = {uid} and ingredient_id = {iid}"
    cur.execute(command)
    conn.commit()

    return 1


# Heapify Grocery Run 

### Expects items as "item_id_1,item_id_2,item_id_3,..."
@app.get("/grocery/run/{items}")
async def heapify_groceries(items: str):
    items = items.split(',')
    uid = items[0]
    items = items[1:]
    q = []

    for iid in items:
        priority = get_priority(0,0) 
        heappush( q, (priority, iid ) )
    q = [ int(x[1]) for x in q ]
    return q

# Get recipe data (full) 
@app.get('/recipe/fetch/title/{rid}')
async def get_recipe_title(rid:int, q: Optional[str] = None, short: bool = False):
    
    command = f"SELECT title FROM recipes as r where r.recipe_id = {rid}"

    cur.execute(command)
    res = cur.fetchone()
    return res[0]

@app.get('/recipe/fetch/ingredients/{rid}')
async def get_recipe_fi(rid:int, q: Optional[str] = None, short: bool = False):
    
    command = f"SELECT full_ingredients FROM recipes as r where r.recipe_id = {rid}"

    cur.execute(command)
    res = cur.fetchone()
    return str(' || '.join(res[0]))

@app.get('/recipe/fetch/instructions/{rid}')
async def get_recipe_instructions(rid:int, q: Optional[str] = None, short: bool = False):
    
    command = f"SELECT instructions FROM recipes as r where r.recipe_id = {rid}"

    cur.execute(command)
    res = cur.fetchone()
    return str(res[0])

# Recommend Recipe 
@app.get('/recipe/recommend/{uid}')
async def recommend_recipe(uid:int, q: Optional[str] = None, short: bool = False):
    
    command = f"SELECT ingredient_id, AVG(fitness) FROM habits WHERE user_id = {uid} GROUP BY ingredient_id ORDER BY AVG(fitness)"
    cur.execute(command)
    res = cur.fetchall()

    print(2%len(res))

    outliers_lo = res[:2%len(res)]
    outliers_hi = res[-(2%len(res)):]
    outliers = outliers_lo + outliers_hi
    
    iid = random.choice(outliers)[0]
    
    command = f"SELECT recipe_id, title,full_ingredients,instructions FROM recipes as r , recipe_ingredients as ri WHERE ri.rid = r.recipe_id AND ri.iid ={iid}"
    cur.execute(command)
    res = cur.fetchall()

    if res == None or len(res)==0:
        command = f"SELECT recipe_id, title,full_ingredients,instructions FROM recipes"
        cur.execute(command)
        return random.choice(cur.fetchall())[0]

    res = sorted(res, key=lambda x : len(x[1]))

    return random.choice(res[0:3%len(res)])[0]
    # returns recipe with a low number of ingredients (more or less)


# Query Recipes 

#Directly
@app.get('/recipe/{rid}')
async def get_recipe(rid:int, q: Optional[str] = None, short: bool = False):
    command = f"SELECT title,full_ingredients,instructions FROM recipes as r WHERE r.recipe_id = {rid}"
    cur.execute(command)
    res = cur.fetchall()

    res = sorted(res, key=lambda x : len(x[1]))

    return random.choice(res[0:3%len(res)])
    # returns recipe with a low number of ingredients (more or less)


#By ingredient
@app.get('/recipe/ingredient/{iid}')
async def query_recipe(iid:int, q: Optional[str] = None, short: bool = False):
    command = f"SELECT title,full_ingredients,instructions FROM recipes as r , recipe_ingredients as ri WHERE ri.rid = r.recipe_id AND ri.iid ={iid}"
    cur.execute(command)
    res = cur.fetchall()

    res = sorted(res, key=lambda x : len(x[1]))

    return random.choice(res[0:3%len(res)])
    # returns recipe with a low number of ingredients (more or less)


#===================================================================================

# Cleanup: 
# conn.commit()
# cur.close()
# conn.close()