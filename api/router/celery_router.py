import json
import psycopg2
from typing import Optional
from fastapi import APIRouter, Query

router = APIRouter()

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

# Helper functions:

def get_all_recipes_with_iid(iid):
    command = f"select * from recipes r where recipe_id = ANY(SELECT rid FROM recipe_ingredients ri WHERE ri.iid = {iid})"
    cur.execute(command)
    return cur.fetchall()

# Routes:

# Throw Grocery item (Fridge)
#TODO

# Add Grocery Item (Fridge)
#TODO

# Remove Grocery Item (Fridge)
#TODO

# Heapify Grocery Run 
@router.get("/grocery/")
async def heapify_grocery_list(q: Optional[str] = Query(None)):
    print(q)
    return q

# Recommend Recipe 
#TODO

# Query Recipes 
#TODO

# Cleanup: 
conn.commit()
cur.close()
conn.close()