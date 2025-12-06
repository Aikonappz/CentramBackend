package com.centram.core.service;

import com.centram.common.dto.BlankRequisitionRequestDto;
import com.centram.common.dto.LoggedInUser;
import com.centram.common.exeception.AppException;
import com.centram.common.exeception.GenericErrorCode;
import com.centram.common.utility.PaginatedList;
import com.centram.core.repository.*;
import com.centram.domain.*;
import com.centram.domain.enumarator.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;

@Service
public class RequisitionService {

    @Autowired
    private RequisitionRepository requisitionRepository;

    @Autowired
    private RequisitionManagerReviewRepository requisitionManagerReviewRepository;

    @Autowired
    RequisitionRecruiterTeamLeadRepository requisitionRecruiterTeamLeadRepository;

    @Autowired
    RequisitionRecruiterReviewRepository requisitionRecruiterReviewRepository;

    @Autowired
    RequisitionCompletedRepository requisitionCompletedRepository;

    @Autowired
    RequisitionMapper requisitionMapper;

    @Autowired
    RequisitionManagerReviewMapper requisitionManagerReviewMapper;

    @Autowired
    RequisitionRecruiterTeamLeadMapper requisitionRecruiterTeamLeadMapper;

    @Autowired
    RequisitionRecruiterReviewMapper requisitionRecruiterReviewMapper;

    @Autowired
    RequisitionCompletedMapper requisitionCompletedMapper;

    @Autowired
    NotificationService notificationService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificationTrackerRepository notificationTrackerRepository;

    @Autowired
    TeamLeadNotificationExtractor teamLeadNotificationExtractor;

    @Autowired
    RequisitionCompletedExtractor requisitionCompletedExtractor;

    @Autowired
    RequisitionNotificationExtractor requisitionNotificationExtractor;

    @Autowired
    ManagerReviewNotificationExtractor managerReviewNotificationExtractor;

    @Autowired
    RequisitionRecruiterReviewExtractor requisitionRecruiterReviewExtractor;

    @Autowired
    PositionRepository positionRepository;

    @Autowired
    DepartmentRepository departmentRepository;


    @Transactional
    public Requisition saveRequisition(Requisition requisition) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(requisition.getId() != null){
            requisition =  updateRequisition(requisition);
        }
        Requisition result = requisitionRepository.save(requisition);

        if (requisition.getHiringManager() != null) {
            User recruiter = userRepository.findByFullName(requisition.getHiringManager())
                    .orElseThrow(() -> new RuntimeException("Hiring Manager not found"));

            boolean alreadyExists = notificationTrackerRepository.existsByOrganisationIdAndBusinessUnitIdAndDivisionIdAndDepartmentIdAndUserId(requisition.getOrganisationId(), requisition.getBusinessUnitId(), requisition.getDivisionId(), requisition.getDepartmentId(), recruiter.getId());

            if (!alreadyExists) {
                NotificationTracker tracker = new NotificationTracker();
                tracker.setOrganisationId(requisition.getOrganisationId());
                tracker.setBusinessUnitId(requisition.getBusinessUnitId());
                tracker.setDivisionId(requisition.getDivisionId());
                tracker.setDepartmentId(requisition.getDepartmentId());
                tracker.setUserId(recruiter.getId());

                notificationTrackerRepository.save(tracker);
            }
        }

        if (requisition.getHeadOfRecruitment()!=null) {
            User recruiter = userRepository.findByFullName(requisition.getHeadOfRecruitment())
                    .orElseThrow(() -> new RuntimeException("Head of Recruitment not found"));

            boolean alreadyExists = notificationTrackerRepository.existsByOrganisationIdAndBusinessUnitIdAndDivisionIdAndDepartmentIdAndUserId(requisition.getOrganisationId(), requisition.getBusinessUnitId(), requisition.getDivisionId(), requisition.getDepartmentId(), recruiter.getId());

            if (!alreadyExists) {
                NotificationTracker tracker = new NotificationTracker();
                tracker.setOrganisationId(requisition.getOrganisationId());
                tracker.setBusinessUnitId(requisition.getBusinessUnitId());
                tracker.setDivisionId(requisition.getDivisionId());
                tracker.setDepartmentId(requisition.getDepartmentId());
                tracker.setUserId(recruiter.getId());

                notificationTrackerRepository.save(tracker);
            }
        }

        notificationService.sendNotification(result, requisitionNotificationExtractor,"FORWARD", loggedInUser.getName());
        return result;
    }

    public Requisition updateRequisition(Requisition requisition) {
        Requisition existing = requisitionRepository.findById(requisition.getId())
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));

        EntityUpdateService.updateEntity(requisition, existing, requisitionMapper);
        return existing;
    }

    public PaginatedList<Requisition> getAllRequisition(String name, Status status, Pageable pageable) {
        return new PaginatedList<Requisition>(requisitionRepository.findAll(pageable));
    }

    public Requisition getByRequisitionId(BigInteger id) {
        return requisitionRepository.findById(id)
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
    }

    @Transactional
    public RequisitionManagerReview saveRequisitionManagerReview(RequisitionManagerReview requisitionManagerReview) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (requisitionManagerReview.getRequisition() != null && requisitionManagerReview.getRequisition().getId() != null) {
            Requisition requisition = requisitionRepository.findById(requisitionManagerReview.getRequisition().getId())
                    .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
            requisitionManagerReview.setRequisition(requisition);
        }

        if (requisitionManagerReview.getId() != null) {
            requisitionManagerReview = updateReview(requisitionManagerReview);
        }
        RequisitionManagerReview result = requisitionManagerReviewRepository.save(requisitionManagerReview);
        notificationService.sendNotification(requisitionManagerReview, managerReviewNotificationExtractor, requisitionManagerReview.getStatus(), loggedInUser.getName());
        return result;
    }

    public RequisitionManagerReview updateReview(RequisitionManagerReview review) {
        RequisitionManagerReview existing = requisitionManagerReviewRepository.findById(review.getId())
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));

        EntityUpdateService.updateEntity(review, existing, requisitionManagerReviewMapper);
        return existing;
    }

    public RequisitionManagerReview getByRequisitionMangerReviewId(BigInteger id) {
        return requisitionManagerReviewRepository.findById(id)
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
    }

    @Transactional
    public RequisitionRecruiterTeamLead saveRequisitionRecruiterTeamLead(RequisitionRecruiterTeamLead requisitionRecruiterTeamLead) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (requisitionRecruiterTeamLead.getRequisition() != null && requisitionRecruiterTeamLead.getRequisition().getId() != null) {
            Requisition requisition = requisitionRepository.findById(requisitionRecruiterTeamLead.getRequisition().getId())
                    .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
            requisitionRecruiterTeamLead.setRequisition(requisition);
        }

        if (requisitionRecruiterTeamLead.getId() != null) {
            requisitionRecruiterTeamLead = updateTeamLead(requisitionRecruiterTeamLead);
        }

        RequisitionRecruiterTeamLead result =  requisitionRecruiterTeamLeadRepository.save(requisitionRecruiterTeamLead);
        notificationService.sendNotification(requisitionRecruiterTeamLead, teamLeadNotificationExtractor, requisitionRecruiterTeamLead.getStatus(), loggedInUser.getName());
        return result;
    }

    public RequisitionRecruiterTeamLead updateTeamLead(RequisitionRecruiterTeamLead teamLead) {
        RequisitionRecruiterTeamLead existing = requisitionRecruiterTeamLeadRepository.findById(teamLead.getId())
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));

        EntityUpdateService.updateEntity(teamLead, existing, requisitionRecruiterTeamLeadMapper);
        return existing;
    }

    public RequisitionRecruiterTeamLead getByRequisitionRecruiterTeamLeadId(BigInteger id) {
        return requisitionRecruiterTeamLeadRepository.findById(id)
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
    }

    @Transactional
    public RequisitionRecruiterReview saveRequisitionRecruiterReview(RequisitionRecruiterReview requisitionRecruiterReview) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (requisitionRecruiterReview.getRequisition() != null && requisitionRecruiterReview.getRequisition().getId() != null) {
            Requisition requisition = requisitionRepository.findById(requisitionRecruiterReview.getRequisition().getId())
                    .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
            requisitionRecruiterReview.setRequisition(requisition);
        }

        if (requisitionRecruiterReview.getId() != null) {
            requisitionRecruiterReview = updateReview(requisitionRecruiterReview);
        }
        RequisitionRecruiterReview result = requisitionRecruiterReviewRepository.save(requisitionRecruiterReview);
        notificationService.sendNotification(requisitionRecruiterReview, requisitionRecruiterReviewExtractor, requisitionRecruiterReview.getStatus(), loggedInUser.getName());
        return result;
    }

    public RequisitionRecruiterReview updateReview(RequisitionRecruiterReview review) {
        RequisitionRecruiterReview existing = requisitionRecruiterReviewRepository.findById(review.getId())
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));

        EntityUpdateService.updateEntity(review, existing, requisitionRecruiterReviewMapper);
        return existing;
    }

    public RequisitionRecruiterReview getByRequisitionRecruiterReviewId(BigInteger id) {
        return requisitionRecruiterReviewRepository.findById(id)
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
    }

    @Transactional
    public RequisitionCompleted saveRequisitionCompleted(RequisitionCompleted completed) {
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (completed.getRequisition() != null && completed.getRequisition().getId() != null) {
            Requisition requisition = requisitionRepository.findById(completed.getRequisition().getId())
                    .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
            completed.setRequisition(requisition);
        }

        if (completed.getId() != null) {
            completed = updateComplete(completed);
        }
        RequisitionCompleted result = requisitionCompletedRepository.save(completed);
        notificationService.sendNotification(completed, requisitionCompletedExtractor, completed.getStatus(), loggedInUser.getName());
        return result;
    }

    public RequisitionCompleted updateComplete(RequisitionCompleted completed) {
        RequisitionCompleted existing = requisitionCompletedRepository.findById(completed.getId())
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));

        EntityUpdateService.updateEntity(completed, existing, requisitionCompletedMapper);
        return existing;
    }

    public RequisitionCompleted getByRequisitionCompletedId(BigInteger id) {
        return requisitionCompletedRepository.findById(id)
                .orElseThrow(() -> new AppException(GenericErrorCode.DATA_NOT_FOUND));
    }

    @Transactional
    public Requisition createOrUpdateFromBlankTemplate(BlankRequisitionRequestDto request) {

        Requisition req;
        LoggedInUser loggedInUser = (LoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (request.getId() != null) {

            req = requisitionRepository.findById(request.getId()).orElseThrow(() -> new RuntimeException("Requisition not found"));

            Position position = positionRepository.findById(req.getPositionId()).orElseThrow(() -> new RuntimeException("Linked position not found"));
            if (position.getDepartment() != null && position.getDepartment().getId() != null) {
                Department dept = departmentRepository.findById(position.getDepartment().getId())
                        .orElseThrow(() -> new RuntimeException("Department not found"));
                position.setDepartment(dept);
            }
            position.setName(request.getJobTitle());
            position.setLocationId(request.getLocationId());
            position.setJobCode(position.getJobCode());
            positionRepository.save(position);

            req.setJobTitle(request.getJobTitle());
            req.setOrganisationId(request.getOrganisationId());
            req.setBusinessUnitId(request.getBusinessUnitId());
            req.setDivisionId(request.getDivisionId());
            req.setDepartmentId(request.getDepartmentId());
            req.setLocationId(BigInteger.valueOf(request.getLocationId()));
            req.setJobDescription(request.getJobDescription());
            req.setRecruiter(request.getRecruiter());
            req.setPayGrade(request.getPayGrade());
            req.setPayRangeMin(request.getPayRangeMin());
            req.setPayRangeMid(request.getPayRangeMid());
            req.setPayRangeMax(request.getPayRangeMax());

            req = requisitionRepository.save(req);
            notificationService.sendNotification(req, requisitionNotificationExtractor, "FORWARD", loggedInUser.getName());
            return req;
        }

        Position position = new Position();
        position.setName(request.getJobTitle());
        position.setStatus(Status.ACTIVE);
        position.setStartDate(LocalDate.now());
        position.setJobCode(request.getJobCode());
        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            position.setDepartment(dept);
        }
        position.setLocationId(request.getLocationId());
        position.setRecruiterName(request.getRecruiter());
        position.setToBeHired(true);

        Position savedPos = positionRepository.save(position);

        req = new Requisition();
        req.setJobTitle(request.getJobTitle());
        req.setOrganisationId(request.getOrganisationId());
        req.setBusinessUnitId(request.getBusinessUnitId());
        req.setDivisionId(request.getDivisionId());
        req.setDepartmentId(request.getDepartmentId());
        req.setLocationId(BigInteger.valueOf(request.getLocationId()));
        req.setJobDescription(request.getJobDescription());
        req.setRecruiter(request.getRecruiter());
        req.setPayGrade(request.getPayGrade());
        req.setPayRangeMin(request.getPayRangeMin());
        req.setPayRangeMid(request.getPayRangeMid());
        req.setPayRangeMax(request.getPayRangeMax());
        req.setRequisitionStatus("Open");
        req.setPositionId(savedPos.getId());
        req.setHiringManager(request.getHiringManager());
        req.setHeadOfBusinessUnit(request.getHeadOfBusinessUnit());
        req.setHeadOfRecruitment(request.getHeadOfRecruitment());

        req = requisitionRepository.save(req);
        notificationService.sendNotification(req, requisitionNotificationExtractor, "FORWARD", loggedInUser.getName());
        return req;
    }
}
