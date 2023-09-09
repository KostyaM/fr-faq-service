FROM amd64/postgres
COPY init.sql /docker-entrypoint-initdb.d
COPY ./migrations/v1_90923.sql /docker-entrypoint-initdb.d/
EXPOSE 5432:5432