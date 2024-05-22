package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.models.Endpoint;

import java.util.List;


public interface EndpointService {

    // Saves the given Endpoint object
    void saveEndpoint(Endpoint endpoint);

    // Retrieves a list of all Endpoint objects
    List<Endpoint> getAllEndpoints();
}
