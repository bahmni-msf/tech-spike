#!/bin/bash

if [ ! -d /opt/bahmni-analytics/conf ]; then
    mkdir -p /opt/bahmni-analytics/conf
fi


manage_user_and_group() {
    #create bahmni user and group if doesn't exist
    USERID=bahmni
    GROUPID=bahmni
    /bin/id -g $GROUPID 2>/dev/null
    [ $? -eq 1 ]
    groupadd bahmni

    /bin/id $USERID 2>/dev/null
    [ $? -eq 1 ]
    useradd -g bahmni bahmni

    echo "hello I am testing scripts"
}

create_analytics_directories() {
    #create analytics directory if it does not exist
    if [ ! -d /home/bahmni/analytics_export ]; then
        mkdir -p /home/bahmni/analytics_export
    fi

    if [ ! -d /opt/bahmni-analytics/log/ ]; then
        mkdir -p /opt/bahmni-analytics/log/
    fi
}

link_directories() {
    #create links
    ln -s /opt/bahmni-analytics/bin/bahmni-analytics /usr/bin/bahmni-analytics
    ln -s /opt/bahmni-analytics/log /var/log/bahmni-analytics
    ln -s /home/bahmni/analytics_export /opt/bahmni-analytics/analytics_export
}

manage_permissions() {
    # permissions
    chown -R bahmni:bahmni /usr/bin/bahmni-analytics
    chown -R bahmni:bahmni /opt/bahmni-analytics
    chown -R bahmni:bahmni /var/log/bahmni-analytics
    chown -R bahmni:bahmni /home/bahmni/analytics_export
}
setup_cronjob() {
    # adding cron job for scheduling the job at 11:30PM everyday
    crontab -u bahmni -l | { cat; echo "30 23 * * * /usr/bin/bahmni-analytics >/dev/null 2>&1"; } | crontab -u bahmni -
}

init_db() {
   RESULT_USER=`psql -U postgres -hlocalhost -tAc "select count(*) from pg_roles where rolname='analytics'"`
   RESULT_DB=`psql -U postgres -hlocalhost -tAc "select count(*) from pg_catalog.pg_database where datname='analytics'"`
   if [ "$RESULT_USER" == "0" ]; then
            echo "creating postgres user - analytics with roles CREATEDB,NOCREATEROLE,SUPERUSER,REPLICATION"
            createuser -Upostgres  -hlocalhost -d -R -s --replication analytics -P;
   fi
   if [ "$RESULT_DB" == "0" ]; then
            echo "creating db - analytics "
            createdb -Uanalytics -hlocalhost analytics;
            psql -U postgres -hlocalhost -tAc "revoke all privileges on  database analytics from public";
   fi
}


manage_user_and_group
create_analytics_directories
link_directories
manage_permissions
setup_cronjob
init_db