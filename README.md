### Scraper Crawling Service - Cermati

----------------------------------------------------------------------------------

    Author: Miftakhul Aziz
    Email: miftakhull.aziz@gmail.com

##### Description
    Saya membuat sebuah service scraper untuk crawling halaman karir cermati 
    menggunakan Java version 8 dan Spring boot, dalam service ini saya provide 
    2 solusi yaitu crawling secara synchronous and asynchronous, pas service ini juga telah saya sediakan 
    Swagger open api untuk memudahkan crawling, selain itu jika ingin menggukan commandLine
    saya suda provide hal itu juga, berikut dokumentasi dari Swagger dan commandLineRunner, ketika proses 
    crawling selesai maka file Json Job List cermati akan disimpan pada directory result

##### Setup Running
    
    I setup using makefile, so that will be simple to run:
    build: make mvn/cleanInstall  
    build Include Update: make mvn/cleanInstallUpdate
    running: make run/scraperService
    build and running: make brun/scraperService

##### Swagger API
    
    After install and running service open this url to view swagger open api, you can crawling using 
    that url, in api just send request param is: https://www.cermati.com/karir
    
    Swagger: http://localhost:8080/swagger-ui/index.html#/

#### Command Line Runner

    java -jar target/scraperService-0.0.1-SNAPSHOT.jar synchronous https://www.cermati.com/karir
    java -jar target/scraperService-0.0.1-SNAPSHOT.jar asynchronous https://www.cermati.com/karir
   
#### Output
    
    the output file crawling will be save into directory result with format Json