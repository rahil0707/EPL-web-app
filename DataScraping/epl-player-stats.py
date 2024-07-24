from bs4 import BeautifulSoup
import pandas as pd
import requests
import time

all_teams = [] # list to store all the contets of the teams

html = requests.get('https://fbref.com/en/comps/9/Premier-League-Stats').text # initiate http get request to the url of the website, request.get method retrieves the contents of the website and the .text method converts it into a string
soup = BeautifulSoup(html, 'lxml') # create a soup object that allows us to find any specific content in the website
table = soup.find_all('table', class_="stats_table")[0] # find the table in the website

# href attribute contains the url to which the anchor tag links so we want this content stored in the links list which is sorted
links = table.find_all('a')
links = [l.get('href') for l in links]
links = [l for l in links if '/squads/' in l] # this line filters the list of URLs to retain only those URLs that contains the substring squads

teams_urls = [f"https://fbref.com{l}" for l in links] #prepends the base url to each of the filtered links and forms a new list called teams_urls which contains all the complete urls pointing to each team

# now we go through each url in the teams_urls list
for team_url in teams_urls:
    teams_name = team_url.split('/')[-1].replace('-Stats', '') # collect the team name
    #repeat same steps as before by first sending a get request to the teams url to get the HTML data content and then parsing it accordingly
    data = requests.get(team_url).text
    soup = BeautifulSoup(data, 'lxml') # need first table on webpage because it has all the goals, assists and every other statistic we need
    stats = soup.find_all('table', class_="stats_table")[0]

    # after getting the team specific data, we want to check if stats and stats.columns exist and then formatting the stats by removing a column
    if stats and stats.columns: stats.columns = stats.columns.droplevel()

    # we then read the html table stats as a list of data frame objects with pandas read_html function and select the first dataframe in the list
    team_data = pd.read_html(str(stats))[0]
    team_data["Team"] = teams_name # we add the team name to the dataframe
    all_teams.append(team_data) # once done with team data, we append it to the all_teams list
    time.sleep(5) # to ensure consistent and accurate data collection without timing out

# once all the team data is collected, we combine all the dataframes into a single dataframe
stat_df = pd.concat(all_teams)
# save the dataframe to a csv file
stat_df.to_csv('epl-player-stats.csv')