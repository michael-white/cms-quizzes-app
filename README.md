# Publish Central

Quick Start:

    $gradle jettyRun
    
    Optional startup flags:
    - Dapp.log=/tmp/debug.log # Enables production like log ( JSON format ) in user selected directory
    - Dmagnolia.root=/tmp/mag_home_temp -Dmagnolia.home=/Users/dianandonov/cms/mag_home_temp # Points the JCR repository location
    - Dserver.port=9090 # Starts the server in selected port

Should see:

    Initializing Log4J
    Initializing Log4J from [WEB-INF/config/default/log4j.xml]
    log4j:WARN Continuable parsing error 21 and column 23
    log4j:WARN The content of element type "log4j:configuration" must match "(renderer*,throwableRenderer?,appender*,plugin*,(category|logger)*,root?,(categoryFactory|loggerFactory)?)".
    ---------------------------------------------
    MAGNOLIA LICENSE
    ---------------------------------------------
    Version number : 5.4.4
    Core version   : 5.4.4
    Build          : 15. January 2016 (rev. c038d45e1e61c2dfeaac7567bbf9c6c4e099a4c7 of UNKNOWN)
    Edition        : Enterprise Edition - custom webapp
    Provider       : Magnolia International Ltd. (info@magnolia-cms.com)
    > Building 93% > :publishing-ui:jettyRun > Running at http://localhost:8080/publishing-ui

username : admin, password: password
