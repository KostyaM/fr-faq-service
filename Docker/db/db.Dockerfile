FROM amd64/postgres
ADD *.sql /docker-entrypoint-initdb.d/
EXPOSE 5432:5432