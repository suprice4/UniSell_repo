package edu.cit.capendit.unisell.admin.service;

import edu.cit.capendit.unisell.admin.dto.AdminReturnResponse;
import edu.cit.capendit.unisell.order.repository.ReturnRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminReturnService {

    @Autowired
    private ReturnRecordRepository returnRecordRepository;

    public List<AdminReturnResponse> listReturns() {
        return returnRecordRepository.findAll()
                .stream()
                .map(AdminReturnResponse::fromEntity)
                .collect(Collectors.toList());
    }
}