#Imports
import requests
from bs4 import BeautifulSoup
import re
import mysql.connector
import os

#Functions
#Returns an integer representing the type of misinformation
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

#Processes one article to get the statement, the category and the url
def processResult(result):
    quote = processQuote(result.find('a').text)
    url = "https://www.politifact.com" + result.find('a')['href']
    image = result.find('img')['alt']
    
    return [quote,url,imgSwitcher(image)]

#Returns the URL of the following page
def getNextPage(URL):
    pageNum = int(URL.split("=")[1])
    print("Page Number " + str(pageNum) + " checked.")#Print which page was checked to help keep track of progress
    pageNum = pageNum + 1
    URL = URL.split("=")[0] + "=" + str(pageNum)
    return URL

#Processes and cleans up the quote
def processQuote(quote):
    quote = quote.strip()
    quote = re.sub('["\'\n]', '', quote)
    return quote

#Returns whether the mySQL server contains the database called databaseName
def containsDatabase(cursor, databaseName):
    cursor.execute("SHOW DATABASES")
    for database in cursor:
        if(database[0] == databaseName.lower()):
            return True
    return False

def containsTable(cursor, tableName):
    cursor.execute("SHOW TABLES")
    for table in cursor:
        if(table[0] == tableName.lower()):
            return True
    return False

#Returns whether the mySQL table contains the result row
def containsArticle(cursor, result):
    sql = "SELECT * FROM " + TABLE_NAME + " WHERE url='" + result[1] + "'"
    cursor.execute(sql)
    result = cursor.fetchall()
    return bool(result), result

#Adds the result to the database
def insertResult(db, cursor, result):
    sql = r"INSERT INTO " + TABLE_NAME + " (quote, url, misinformationType, newlyScraped) VALUES ('%s', '%s', '%d', TRUE)" % tuple(result)
    cursor.execute(sql)
    db.commit()

#Changes the result in the database to update it
def alterTable(db, cursor, result):
    sql = r"UPDATE " + TABLE_NAME + " SET quote = '" + result[0] + "', misinformationType = '" + str(result[2]) + "', newlyScraped = TRUE WHERE url = '" + result[1] +"'"
    print(sql)
    cursor.execute(sql)
    db.commit()

#Main
#Constants
DATABASE_NAME = "Misinfo_DB"
TABLE_NAME = "Misinfo_T"
MAXIMUM_NUM_PAGES = 636
#Secret Environment Variables
MY_SQL_HOST = os.getenv('MY_SQL_HOST')
MY_SQL_USER = os.getenv('MY_SQL_USER')
MY_SQL_PASSWORD = os.getenv('MY_SQL_PASSWORD')

#Initialize Database and Table
SQLdatabase = mysql.connector.connect(
    host = MY_SQL_HOST,
    user = MY_SQL_USER,
    password = MY_SQL_PASSWORD
)
SQLcursor = SQLdatabase.cursor()

if not containsDatabase(SQLcursor, DATABASE_NAME):
   SQLcursor.execute("CREATE DATABASE " + DATABASE_NAME)

SQLdatabase = mysql.connector.connect(
    host = MY_SQL_HOST,
    user = MY_SQL_USER,
    password = MY_SQL_PASSWORD,
    database=DATABASE_NAME
)
SQLcursor = SQLdatabase.cursor()

if not containsTable(SQLcursor, TABLE_NAME):
   SQLcursor.execute("CREATE TABLE " + TABLE_NAME + " (quote MEDIUMTEXT, url VARCHAR(255) PRIMARY KEY, misinformationType INT, newlyScraped BOOL)")

#Webscraping 
URL = 'https://www.politifact.com/factchecks/list/?page=1' #URL of the first page
foundItem = False #Boolean for when a previously scraped item was found
countdownWhenFoundItem = 10 #Number of pages to check after a previously scraper item was found, to check for updates to articles
for i in range(MAXIMUM_NUM_PAGES):
    results = processSet(URL) #Returns all the results from that URL
    for result in results:
        articleExists, article = containsArticle(SQLcursor, result)
        if articleExists: #If we've reached an already checked statement, will check if we need to alter the table, and will check only 10 more pages.
            if result != list(article[0]):
                alterTable(SQLdatabase, SQLcursor, result)
            if not foundItem:
               foundItem = True
            continue
        if result[2] != 'Invalid': #If the misinformation type is not invalid
            insertResult(SQLdatabase, SQLcursor, result)
    if countdownWhenFoundItem == 0:
        break
    if foundItem:
        countdownWhenFoundItem -= 1
    URL = getNextPage(URL) #Goes to the next page
