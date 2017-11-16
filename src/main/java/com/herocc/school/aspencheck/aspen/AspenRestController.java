package com.herocc.school.aspencheck.aspen;

import com.herocc.school.aspencheck.GenericRestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("aspen")
public abstract class AspenRestController extends GenericRestController {

}
