package com.herocc.school.aspencheck.aspen;

import com.herocc.school.aspencheck.AspenCheck;
import com.herocc.school.aspencheck.District;
import com.herocc.school.aspencheck.ErrorInfo;
import com.herocc.school.aspencheck.JSONReturn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/{district-id}/aspen")
public class AspenController {
  
  @RequestMapping("/checkLogin")
  public ResponseEntity<JSONReturn> serveSchedule(@PathVariable(value="district-id") String districtName,
                                                  @RequestHeader(value="ASPEN_UNAME") String u,
                                                  @RequestHeader(value="ASPEN_PASS") String p){


    
    AspenWebFetch a = new AspenWebFetch(districtName, u, p);
    
    return new ResponseEntity<>(new JSONReturn(a.areCredsCorrect(), new ErrorInfo()), HttpStatus.OK);
  }
  
}
