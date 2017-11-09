#Baking Java EE MicrcoPi

To start the ticker service:

    java -jar StockTicker/target/StockTicker-1.0-SNAPSHOT-microbundle.jar --autobindhttp &
    
    
To start the UI service:

    java -jar StockWeb/target/StockWeb-1.0-SNAPSHOT-microbundle.jar --autobindhttp

