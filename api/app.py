from fastapi import FastAPI 
from router import celery_router

app = FastAPI()
app.include_router(celery_router.router, prefix='/grocery')

