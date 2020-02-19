package com.genomics.pedigreeaudit.api;


import com.genomics.pedigreeaudit.model.AuditResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
public class PedigreeAuditController {

    private Logger logger = LoggerFactory.getLogger(PedigreeAuditController.class);
    private final HttpServletRequest request;

    @Autowired
    public PedigreeAuditController(@SuppressWarnings("SpringJavaAutowiringInspection") HttpServletRequest request) {

        this.request = request;
    }

    @GetMapping(value = "/audit/{testRequestId}", produces = { "application/json" })
    public ResponseEntity getPedigree (
             @PathVariable(value = "testRequestId") String testRequestId) {

        logger.info("Received request to retrieve a saved pedigree information " + testRequestId);

        // do some processing
        logger.info("Request processed. ReferralId=" + testRequestId);
        return new ResponseEntity<>(new AuditResponse(testRequestId, "Valid"), HttpStatus.OK);
    }
}
