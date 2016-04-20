AssetLibrary
============

An easy way to track assets using QR Codes, and self checkout.
It also has a built in QRCode generator so that you can generate for other stuff too.


How To Build
============

    ant -f master.xml



How To Run
============

    ant -f master.xml
    cd ./dist
    unzip AssetLibrary.zip
    nohup ./run_asset_library.sh > webserver.log 2>&amp;1&amp;

Navigate to:

    http://localhost:9800/index.html
    
