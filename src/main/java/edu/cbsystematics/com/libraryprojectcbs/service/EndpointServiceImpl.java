package edu.cbsystematics.com.libraryprojectcbs.service;

import edu.cbsystematics.com.libraryprojectcbs.models.Endpoint;
import edu.cbsystematics.com.libraryprojectcbs.repository.EndpointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EndpointServiceImpl implements EndpointService {

    private final EndpointRepository endpointRepository;

    @Autowired
    public EndpointServiceImpl(EndpointRepository endpointRepository) {
        this.endpointRepository = endpointRepository;
    }

    @Override
    public void saveEndpoint(Endpoint endpoint) {
        // Get the URL of the Endpoint
        String url = endpoint.getUrl();
        // Check if an Endpoint with the same URL already exists in the database
        Endpoint existingEndpoint = endpointRepository.findByUrl(url);

        // If an existing Endpoint is found - increment
        if (existingEndpoint != null) {
            existingEndpoint.setCountURL(existingEndpoint.getCountURL() + 1);
            endpointRepository.save(existingEndpoint);
        } else {
            // If no existing Endpoint is found - set the call count to 1
            endpoint.setCountURL(1);
            endpointRepository.save(endpoint);
        }
    }

    @Override
    public List<Endpoint> getAllEndpoints() {
        return endpointRepository.findAll();
    }

}