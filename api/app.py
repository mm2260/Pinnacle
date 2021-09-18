from fastapi import FastAPI 
from router import grocery_router

app = FastAPI()
app.include_router(grocery_router.router, prefix='/grocery')
