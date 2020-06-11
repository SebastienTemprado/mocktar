package fr.stemprado.apps.mocktar.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import fr.stemprado.apps.mocktar.beans.DatabaseSequence;
import fr.stemprado.apps.mocktar.exceptions.SequenceException;

@Service
public class SequenceGeneratorService {

    @Autowired
    private MongoOperations mongoOperations;
    
    public long generateSequence(String seqName) {
        Query query = new Query(Criteria.where("_id").is(seqName));
        Update update = new Update();
        update.inc("seq", 1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true).upsert(true);

        DatabaseSequence seqId = mongoOperations.findAndModify(query, update, options, DatabaseSequence.class);
        
        if (seqId == null) {
            throw new SequenceException("Unable to get sequence id for key : " + seqName);
        }

        return seqId.getSeq();
    }
}