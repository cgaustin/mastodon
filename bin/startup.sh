#!/bin/bash

INDEX=/usr/share/nginx/html/index.html
VERSION=`bin/version`

sed -i "s!<ARDHOST>!${ARD_HOST}!g"    ${INDEX}
sed -i "s!<CHIPMUNKHOST>!${CHIPMUNK_HOST}!g"  ${INDEX}
sed -i "s!<AUXHOST>!${AUX_HOST}!g"    ${INDEX}
sed -i "s!<INGESTHOST>!${ARD_HOST}!g" ${INDEX}
sed -i "s!<INGESTPARTITIONING>!${PARTITION_LEVEL}!g" ${INDEX}

if [ ${DATA_TYPE} == "ard" ]; then
  #uncomment javascript populating year select dropdowns
  sed -i "s/\/\/ARD//g" ${INDEX}
fi

/usr/sbin/nginx -g 'daemon off;' & java -jar /app/target/lcmap-mastodon-${VERSION}-standalone.jar
