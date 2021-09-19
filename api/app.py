import json
import psycopg2
from heapq import heappop, heappush
from typing import Optional, List
from fastapi import FastAPI, Query

app = FastAPI()

#===================================================================================

# Initialize DB connection:
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
#TODO

# Add Grocery Item (Fridge)
#TODO

# Remove Grocery Item (Fridge)
#TODO

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

# Recommend Recipe 
#TODO

# Query Recipes 
#TODO

#===================================================================================

# Cleanup: 
# conn.commit()
# cur.close()
# conn.close()