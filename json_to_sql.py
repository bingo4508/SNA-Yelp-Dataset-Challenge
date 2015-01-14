__author__ = 'bingo4508'

import json

from sqlalchemy import create_engine
from sqlalchemy import create_engine
from sqlalchemy import Column, Integer, String, Float, Boolean
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import ForeignKey
from sqlalchemy.orm import relationship, backref
from sqlalchemy.orm import sessionmaker
from util import *

Base = declarative_base()


class Business(Base):
    __tablename__ = 'Business'

    type = Column(String)
    business_id = Column(String, primary_key=True)
    name = Column(String)
    neighborhoods = Column(String)  # List
    full_address = Column(String)
    city = Column(String)
    state = Column(String)
    latitude = Column(Float)
    longitude = Column(Float)
    stars = Column(Float)
    review_count = Column(Integer)
    categories = Column(String)  # List
    open = Column(Boolean)
    hours = Column(String)  # Map
    attributes = Column(String)  # Map

    # def __repr__(self):
    #    return "User('%s','%s', '%s')" % \
    #        (self.name, self.username, self.password)


if __name__ == '__main__':
    engine = create_engine('sqlite:///data/yelp.db', echo=True)
    Base.metadata.create_all(engine)

    Session = sessionmaker(bind=engine)
    session = Session()

    li = load_json('data/Business.txt')
    for e in li:
        r = Business()
        r.type = e['type']
        r.business_id = e['business_id']
        r.name = e['name']
        r.neighborhoods = json.dumps(e['neighborhoods'])
        r.full_address = e['full_address']
        r.city = e['city']
        r.state = e['state']
        r.latitude = e['latitude']
        r.longitude = e['longitude']
        r.stars = e['stars']
        r.review_count = e['review_count']
        r.categories = json.dumps(e['categories'])
        r.open = e['open']
        r.hours = json.dumps(e['hours'])
        r.attributes = json.dumps(e['attributes'])
        session.add(r)
    session.commit()
