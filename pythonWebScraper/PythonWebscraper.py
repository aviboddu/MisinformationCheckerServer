#Imports
import requests
from bs4 import BeautifulSoup
import pandas
import re
import numpy as np
import os.path
from os import path

#Functions
#Returns an integer representing the category of misinformation
def imgSwitcher(argument):
    switcher = {
        'pants-fire':0,
        'false':1,
        'barely-true':2,
        'half-true':3,
        'mostly-true':4,
        'true':5
        }
    return switcher.get(argument, "Invalid")

#Returns all the processed articles from one page of Politifact's 'latest entries'
def processSet(URL):
    page = requests.get(URL)
    soup = BeautifulSoup(page.content, 'html.parser')
    results = soup.find_all('div', class_='m-statement__body')
    listResults = []
    for result in results:
        listResults.append(processResult(result))
    return listResults

#Processes one article to get the statement, the category and the link
def processResult(result):
    quote = result.find('a').text.strip()
    quote = re.sub('"', '',quote)
    quote = re.sub('\n', '', quote)
    link = "https://www.politifact.com" + result.find('a')['href']
    image = result.find('img')['alt']
    
    return [quote,link,imgSwitcher(image)]

#Returns the URL of the following page
def getNextPage(URL):
    pageNum = int(URL.split("=")[1])
    print("Page Number " + str(pageNum) + " checked.")#Print which page was checked to help keep track of progress
    pageNum = pageNum + 1
    URL = URL.split("=")[0] + "=" + str(pageNum)
    return URL

#Main
dict = {'Statement':[],
        'URL':[],
        'Category':[]
        }
if path.exists('Database.csv'):
    df = pandas.read_csv(r'Database.csv')
    latestResult = df.iloc[0]
else:
    df = pandas.DataFrame(dict)
    latestResult = ["No File","a",10.0]

df2 = pandas.DataFrame(dict)
URL = 'https://www.politifact.com/factchecks/list/?page=1'#URL of the first page
numberOfPages = 636
foundItem = False
for i in range(numberOfPages):
    results = processSet(URL)#Returns all the results from that URL
    for result in results:
        if result[0] == latestResult[0]:#If we've reached an already checked statement, won't check any more.
            foundItem = True
            break
        if result[2] != 'Invalid':
            df2.loc[len(df2.index)] = result
    if foundItem:
        break
    URL = getNextPage(URL)#Goes to the next page
df = df2.append(df)
df.to_csv(r'Database.csv', index=False)#Stores everything as a .csv file
print("Articles Processed:")
print(len(df.index))
