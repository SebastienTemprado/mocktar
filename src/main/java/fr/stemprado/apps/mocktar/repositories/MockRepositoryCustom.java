package fr.stemprado.apps.mocktar.repositories;

import fr.stemprado.apps.mocktar.beans.Mock;

public interface MockRepositoryCustom {
    
    Mock myCustomQuery(String request);
}