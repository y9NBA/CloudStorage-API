package org.y9nba.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y9nba.app.model.*;
import org.y9nba.app.service.impl.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("test")
public class TestController {
    private final Logger logger = Logger.getLogger(TestController.class.getName());

    private final UserServiceImpl userService;
    private final UserRoleServiceImpl userRoleService;
    private final AuditLogServiceImpl auditLogService;
    private final FileServiceImpl fileService;
    private final FileAccessServiceImpl fileAccessService;

    public TestController(UserServiceImpl userService, UserRoleServiceImpl userRoleService, AuditLogServiceImpl auditLogService, FileServiceImpl fileService, FileAccessServiceImpl fileAccessService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.auditLogService = auditLogService;
        this.fileService = fileService;
        this.fileAccessService = fileAccessService;
    }

    @GetMapping()
    public ResponseEntity<?> getTest() {
        return ResponseEntity.ok(userService.findById(1L));
    }

    @GetMapping("/1")
    public ResponseEntity<?> getTest1() {
        return ResponseEntity.ok(fileService.findById(1L));
    }

    @GetMapping("/2")
    public ResponseEntity<?> getTest2() {
        return ResponseEntity.ok(fileAccessService.findById(1L));
    }
}
