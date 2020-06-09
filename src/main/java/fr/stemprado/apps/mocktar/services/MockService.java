package fr.stemprado.apps.mocktar.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.stemprado.apps.mocktar.beans.Mock;
import fr.stemprado.apps.mocktar.repositories.MockRepository;


@Service
public class MockService {

    @Autowired
    private MockRepository dao;

    public Mock getMock(String name) {
        return dao.findByName(name);
    }

    public void postMock(Mock mock) {
        dao.insert(mock);
    }
}