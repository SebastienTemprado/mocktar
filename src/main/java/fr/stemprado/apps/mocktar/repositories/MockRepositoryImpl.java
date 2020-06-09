package fr.stemprado.apps.mocktar.repositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import fr.stemprado.apps.mocktar.beans.Mock;


public class MockRepositoryImpl implements MockRepositoryCustom {

    private final MongoTemplate mongoTemplate;
 
	@Autowired
	public MockRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public void saveMock(Mock mock) {
        mongoTemplate.save(mock);
	}

	@Override
	public Mock myCustomQuery(String request) {
		Query query = new Query();
        query.addCriteria(Criteria.where("request").is(request));
		return mongoTemplate.findOne(query, Mock.class);
	}
}