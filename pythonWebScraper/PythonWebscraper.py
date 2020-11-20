#Imports
import requests
from bs4 import BeautifulSoup
import pandas
import re

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

#Processes each article to get the statement, the category and the link
def processResult(result):
    quote = result.find('a').text.strip()
    quote = re.sub('"', '',quote)
    quote = re.sub('\n', '', quote)
    link = "https://www.politifact.com" + result.find('a')['href']
    image = result.find('img')['alt']
    
    return [quote,link,imgSwitcher(image)]

#Returns the URL of the following page
def getNextPage(URL):
    pageNum = int(URL.split("=")[1]) + 1
    URL = URL.split("=")[0] + "=" + str(pageNum)
    return URL

#Main
dict = {'Statement':[],
        'URL':[],
        'Category':[]
        }
df = pandas.DataFrame(dict)#Creates a table with three columns: the statement, category and link
URL = 'https://www.politifact.com/factchecks/list/?page=1'#URL of the first page
numberOfPages = 150
for i in range(numberOfPages):
    results = processSet(URL)#Returns all the results from that URL
    for result in results:
        if result[2] != 'Invalid':
            df.loc[len(df.index)] = result#Adds each result to the table if it is not invalid
    URL = getNextPage(URL)#Goes to the next page
df.to_csv(r'Database.csv', index=False)#Stores everything as a .csv file
