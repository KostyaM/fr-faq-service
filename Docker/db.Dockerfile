FROM amd64/postgres
COPY init.sql /docker-entrypoint-initdb.d/
EXPOSE 5432:5432