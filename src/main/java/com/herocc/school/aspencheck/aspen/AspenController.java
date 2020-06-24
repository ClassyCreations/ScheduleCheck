package com.herocc.school.aspencheck.aspen;

import com.herocc.school.aspencheck.ErrorInfo;
import com.herocc.school.aspencheck.JSONReturn;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/{district-id}/aspen")
public class AspenController {

  @Operation(summary = "Check Aspen Credential Validity")
  @GetMapping("/checkLogin")
  public ResponseEntity<JSONReturn> serveSchedule(@PathVariable(value="district-id") String districtName,
                                                  @RequestHeader(value="ASPEN_UNAME") String u,
                                                  @RequestHeader(value="ASPEN_PASS") String p){



    AspenWebFetch a = new AspenWebFetch(districtName, u, p);

    return new ResponseEntity<>(new JSONReturn(a.areCredsCorrect(), new ErrorInfo()), HttpStatus.OK);
  }

}
