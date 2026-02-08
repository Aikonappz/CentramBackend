package com.centram.core.service;

import com.centram.core.repository.JobPortalCandidateRepository;
import com.centram.domain.JobPortalCandidate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class JobPortalCandidateAuthService {
    
    private final JobPortalCandidateRepository jobPortalCandidateRepository;
    private final PasswordEncoder passwordEncoder;

    public JobPortalCandidateAuthService(JobPortalCandidateRepository jobPortalCandidateRepository, PasswordEncoder passwordEncoder) {
        this.jobPortalCandidateRepository = jobPortalCandidateRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public JobPortalCandidate register(String name, String email, String password) {

        if (jobPortalCandidateRepository.existsByEmail(email)) {
            throw new RuntimeException("Account already exists. Please login.");
        }

        JobPortalCandidate candidate = new JobPortalCandidate();
        candidate.setName(name);
        candidate.setEmail(email);
        candidate.setPassword(passwordEncoder.encode(password));
        candidate.setActive(true);

        return jobPortalCandidateRepository.save(candidate);
    }

    public JobPortalCandidate login(String email, String password) {

        JobPortalCandidate candidate = jobPortalCandidateRepository.findByEmail(email).orElseThrow(() ->
                        new RuntimeException("Account does not exist. Please register."));

        if (!passwordEncoder.matches(password, candidate.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!Boolean.TRUE.equals(candidate.getActive())) {
            throw new RuntimeException("Account is disabled");
        }

        return candidate;
    }
}
