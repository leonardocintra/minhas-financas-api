restart-docker:
	@sudo docker-compose stop
	@sudo docker-compose rm -f
	@sudo docker-compose up -d	
