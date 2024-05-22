package edu.cbsystematics.com.libraryprojectcbs.config.security;

import edu.cbsystematics.com.libraryprojectcbs.models.Endpoint;
import edu.cbsystematics.com.libraryprojectcbs.service.EndpointService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class EndpointCounterFilter implements Filter {

    private final EndpointService endpointService;

    @Autowired
    public EndpointCounterFilter(EndpointService endpointService) {
        this.endpointService = endpointService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {

        // Check if the request is an instance of HttpServletRequest
        if (request instanceof HttpServletRequest httpRequest) {
            // Get the URL
            String url = httpRequest.getRequestURI();

            // Count the number URL
            if (endpointService != null) {
                endpointService.saveEndpoint(new Endpoint(url, 1));
            }
        }

        // Continue the filter
        chain.doFilter(request, response);
    }

}
