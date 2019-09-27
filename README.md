# Movies Search Service

##### Assignee: Christidis Nikolaos (chriniko - nick.christidis@yahoo.com)


### Description

This assessment is based on the popular website IMDb which offers movies and TV shows information. They have kindly made their dataset publicly available at IMDb Datasets.

Your mission, should you choose to accept it, is to write a web application in Java or Scala that can fulfil the following requirements:

* Requirement #1 (easy):
    IMDb copycat: Present the user with endpoint for allowing them to search by movie’s primary title or original title. 
    The outcome should be related information to that title, including cast and crew.

* Requirement #2 (easy):
    Top rated movies: Given a query by the user, you must provide what are the top rated movies for a genre (If the user searches horror, 
    then it should show a list of top rated horror movies).

* Requirement #3 (easy):
    Typecasting: Given a query by the user, where he/she provides an actor/actress name, 
    the system should determine if that person has become typecasted (at least half of their work is one genre).

* Requirement #4 (easy):
    Find the coincidence: Given a query by the user, where the input is two actors/actresses names, the application replies with a list of movies or TV shows that both people have shared.

* Requirement #5 (difficult):
    Six degrees of Kevin Bacon: Given a query by the user, you must provide what’s the degree of separation between the person 
    (e.g. actor or actress) the user has entered and Kevin Bacon.

* Stretch goal:
    1) Add a UI to requirement #1 to present the result of such endpoint
    (you can also use OMDb,to include more details than the one present in the database, such as poster image,
    title description and so on).

    2) Generalise for n amount of names on requirement #4.


### Exercise Status
* Requirement 1 [DONE]
* Requirement 2 [DONE]
* Requirement 3 [DONE]
* Requirement 4 [DONE]
* Requirement 5 [DONE]

* Stretch Goal 1 [NOT DONE]
* Stretch Goal 2 [DONE]

### Required Dependencies (0/2) --- Imdb Csv Dataset
* You need to download Imdb csv dataset from here: [https://www.imdb.com/interfaces/](), [https://datasets.imdbws.com/]()

    * You should download the following files:
        * `name.basics.tsv.gz`
        * `title.akas.tsv.gz`
        * `title.basics.tsv.gz`
        * `title.crew.tsv.gz`
        * `title.episode.tsv.gz`
        * `title.principals.tsv.gz`
        * `title.ratings.tsv.gz`
        
    * And put them in `src/main/resources/data` folder, so the `DataLoader.java#initZippedFilesBindings()` method could work correctly


### Required Dependencies (1/2) --- How to prepare the MySQL Database (for Requirements 1-4)

* Have a running MySQL Server, so execute: `docker-compose up` in order to start MySQL

* Time to load the data from CSV Imdb files to MySQL:

    * One way is to import the dump I ship together with the assignment, folder which contains dump: `TODO`

    * The other way is to:
    
        * Set the value of `output.directory` in `application.properties` 
          to a correct one, so application is able to extract the compressed files of IMDb.

        * Change property of `spring.jpa.hibernate.ddl-auto` in application.properties to `create`
        
        * Start the application

        * Then hit via HTTP, POST method on: `http://localhost:1711/data/load`,
          which starts an ETL job (from CSV -> to MySQL), it takes around: [58 minutes - 1 hour]
          ```text
                (Note: 2018-11-04 21:13:28.189  INFO 7086 --- [pool-1-thread-1] c.c.l.movies.service.data.DataLoader     : Total time to load data in ms: 3524434)
          ```
        
        * IMPORTANT, After successful etl operation, do not forget to change `spring.jpa.hibernate.ddl-auto` in `application.properties` to `validate` or `none`
        
        * IMPORTANT, Do not forget to go to `MySQL Indexes Created` section in this file and create all the indexes, it is vital for response.


### Required Dependencies (2/2) --- How to prepare the Neo4j (for Requirement 5, shortestPath)

* Run Neo4j via docker: 
    ```docker
      docker run \
              --publish=7474:7474 --publish=7687:7687 \
              --volume=$HOME/neo4j/data:/data \
              --volume=$HOME/neo4j/logs:/logs \
              --volume=$HOME/plugins:/plugins \
              --volume=$HOME/import:/var/lib/neo4j/import \
              neo4j:3.0
    ```
    
        
* Common credentials: `neo4j/neo4j` || `neo4j/1234`
        
* Time to load the data from MySQL to Neo4j:

    * One way is to cd into the $HOME/neo4j/data (see docker command) and replace the contents with those I have inside folder: Dump_for_Neo4j,
      do the same for $HOME/neo4j/logs
    
    * The other way is to:
        * Hit via HTTP, `POST` method on: `http://localhost:1711/acquaintance-links/prepare`,
          which starts an `ETL job (from MySQL -> to Neo4j)`, it takes a lot of time in order to complete
          so I advise you to wait for 5-8 completed steps (see logs) and then play with the degrees requirement.
          Keep in mind that the integration test for Req.5 (Requirement5_ITSpec.groovy) might not pass due to different
          loaded dataset.
        


### How to run integration tests

* IMPORTANT, first you should have a running and populated(from etl operation, csv->sql) MySQL db.

* Test case `Requirement5_ITSpec.groovy` might not pass due to different loaded dataset inside Neo4j, so it is ignored, but it is recommended to play with it.

* Execute <code>mvn clean verify</code>


### How to manual test

* Load data (csv -> mysql) via http endpoint: 
    * localhost:1711/data/load

* Requirement 1, use (GET): 
    * `localhost:1711/search/titles/?query=title:Ocean's Eleven&full-fetch=true`
    * `localhost:1711/search/titles/?query=title:alpha&full-fetch=true`


* Requirement 2, use (GET):     
    * `localhost:1711/search/titles/?query=genre:horror&full-fetch=false`
    * `localhost:1711/search/titles/?query=genre:Horror&full-fetch=false`
         
                       
* Requirement 3, use (GET):     
    * `localhost:1711/search/names/?name=Will Smith&full-fetch=false`
    * `localhost:1711/search/names/?name=Charles Bronson&full-fetch=false`
    * `localhost:1711/search/names/?name=Steve McQueen&full-fetch=false`
    * `localhost:1711/search/names/?name=Danny DeVito&full-fetch=false`
        
                           
* Requirement 4, use: 

    * POST with body:
       ```json
           {
            "names":["Angelina Jolie", "Brad Pitt"]
           }
        ```
       To URL: `localhost:1711/search/names/coincidence`
   
    * POST with body:
       ```json
            {
             "names":["Orlando Bloom", "Johnny Depp"]
            }    
        ```
        To URL: `localhost:1711/search/names/coincidence`
          
    * POST with body:
       ```json
             {
              "names":["Brad Pitt", "Robert Redford"]
             }    
        ```
        To URL: `localhost:1711/search/names/coincidence`
          
    * POST with body:
       ```json
            {
              "names":["George Clooney", "Brad Pitt"]
             }    
        ```     
        To URL: `localhost:1711/search/names/coincidence`
             
    * POST with body:
       ```json
             {
              "names":["George Clooney", "Matt Damon"]
             }    
        ```
        To URL: `localhost:1711/search/names/coincidence`
             
    * POST with body:
       ```json
              {
               "names":["George Clooney", "Matt Damon", "Brad Pitt"]
              }   
        ```    
         To URL: `localhost:1711/search/names/coincidence`
             
    * POST with body:
       ```json
             {
              "names":["John Travolta", "Uma Thurman", "Samuel L. Jackson", "Bruce Willis"]
             }    
       ``` 
        To URL: `localhost:1711/search/names/coincidence`
             
    * POST with body:
        ```json
             {
                "names":["Al Pacino", "Robert De Niro", "Val Kilmer", "Jon Voight"]
             }
        ```
         To URL: `localhost:1711/search/names/coincidence`
        

* Requirement 5, POST `localhost:1711/acquaintance-links` with body:
    ```json
          {
            "sourceFullName":"Matt Damon",
            "targetFullName":"Kevin Bacon"
           } 
    ```
      


### MySQL Indexes Created
1) aka -> title_id
    ```sql
    ALTER TABLE `movies_search_service_db`.`aka` 
    ALTER TABLE `movies_search_service_db`.`aka` 
    ADD INDEX `title_id_idx` (`title_id` ASC);
    ```

2) basic -> primary_title
    ```sql
    ALTER TABLE `movies_search_service_db`.`basic` 
    ADD INDEX `primary_title_idx` (`primary_title` ASC);
    ```


3) basic -> original_title
    ```sql
    ALTER TABLE `movies_search_service_db`.`basic` 
    ADD INDEX `original_title_idx` (`original_title` ASC);
    ```


4) episode -> parent_tconst
    ```sql
    ALTER TABLE `movies_search_service_db`.`episode` 
    ADD INDEX `parent_tconst_idx` (`parent_tconst` ASC);
    ```


5) basic -> genres
    ```sql
    ALTER TABLE `movies_search_service_db`.`basic` 
    ADD INDEX `genres_idx` (`genres` ASC);
    ```


6) principal -> tconst
    ```sql
    ALTER TABLE `movies_search_service_db`.`principal` 
    ADD INDEX `principal_tconst_idx` (`tconst` ASC);
    ```


7) name -> primary_name
    ```sql
    ALTER TABLE `movies_search_service_db`.`name` 
    ADD INDEX `primary_name_idx` (`primary_name` ASC);
    ```


8) principal -> nconst
    ```sql
    ALTER TABLE `movies_search_service_db`.`principal` 
    ADD INDEX `principal_nconst_idx` (`nconst` ASC);
    ```
##


### Distinct Genres in Database
0) "Film-Noir"
1) "Action"
2) "War"
3) "History"
4) "Western"
5) "Documentary"
6) "Sport"
7) "Thriller"
8) "News"
9) "Biography"
10) "Adult"
11) "Comedy"
12) "Mystery"
13) "Musical"
14) "Short"
15) "Talk-Show"
16) "Adventure"
17) "Horror"
18) "Romance"
19) "Sci-Fi"
20) "Drama"
21) "Music"
22) "Game-Show"
23) "Crime"
24) "Fantasy"
25) "Animation"
26) "Family"
27) "Reality-TV"

##

### Distinct categories in principal table.
* `select distinct category from principal limit 30590275;`

* Results:
  * self
  * director
  * cinematographer
  * composer
  * producer
  * editor
  * actor
  * actress
  * writer
  * production_designer
  * archive_footage
  * archive_sound
  