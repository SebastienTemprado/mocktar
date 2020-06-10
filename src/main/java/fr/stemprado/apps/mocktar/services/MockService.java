package fr.stemprado.apps.mocktar.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.stemprado.apps.mocktar.beans.Mock;
import fr.stemprado.apps.mocktar.repositories.MockRepository;


@Service
public class MockService {

    @Autowired
    private MockRepository dao;

    public List<Mock> getMocks(String name) {
        List<Mock> mocks = new ArrayList<>();
        if (name == null) {
            mocks.addAll(dao.findAll());
        } else {
            Mock mock = dao.findByName(name);
            if (mock != null) {
                mocks.add(mock);
            }
        }
        return mocks;
    }

    public void postMock(Mock mock) {
        dao.insert(mock);
    }
}