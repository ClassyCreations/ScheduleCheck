<?php
$schedName = "sched.txt";
$jarName = "ScheduleCheck-1.5-SNAPSHOT.jar";
$cookieName = "schedulecheck_user_guid";

header('Cache-Control: no-cache, must-revalidate'); // No Cache
header('Content-type: application/json'); // JSON Type
header('Access-Control-Allow-Origin: *'); // Allow scripts to call me

function main(){
  global $schedName, $cookieName;
  $refreshTime = 120;
  $weekend = date('w') == 0 || date('w') == 6;
  $schoolInSession = date('Hi') < '0630' || date('Hi') > '1415'; // Usual time school is in session
  if ($weekend || !$schoolInSession) {
    $refreshTime = 10 * 60; // 10 minutes
  }
  
  if (!isset($_COOKIE[$cookieName])) setcookie($cookieName, guidv4(random_bytes(16)), 2147483647);
  buildAndCopyJar();
  
  $uname = $_POST['username'];
  $pass = $_POST['password'];
  
  $json = json_decode(getCachedSched());
  if (!$uname == null && !$pass == null){ // If Username and Password are given, use custom results
    echo runAspenJar($uname, $pass, "/dev/null", false, false);
  } else if (time() - $json->{'asOf'} > $refreshTime) { // If cache expired, start renewal process and serve cache
    error_log("Cached time: " . $json->{'asOf'} . " is greater than " . time() . " - " . $refreshTime . ", refreshing", 0);
    if (getCachedSched() != null) {
      echo getCachedSched(); // Use cached schedule if it exists
      runAspenJar(getenv('ASPEN_UNAME'), getenv('ASPEN_PASS'), $schedName, true, true);
    } else { // And if not just make them wait for output
      echo runAspenJar(getenv('ASPEN_UNAME'), getenv('ASPEN_PASS'), $schedName, false, true);
    }
  } else { // If not user specific and cache not expired
    echo getCachedSched();
  }
}

function getCachedSched(){
  global $schedName;
  if (!file_exists($schedName)) {
    return null;
  }
  $handle = fopen($schedName, "r");
  $contents = fread($handle, filesize($schedName));
  fclose($handle);
  return $contents;
}

/**
 *
 * @param $username String Aspen Username
 * @param $pass String Aspen Password
 * @param $file String File path to output to
 * @param $async Boolean Run async (without output)
 * @param $hide Boolean Hide private / sensitive data
 * @return mixed
 */
function runAspenJar($username, $pass, $file, $async, $hide){
  global $jarName;
  
  $command = "java -jar $jarName -f $file -u $username -p $pass";
  if ($hide == true) $command .= " --hidePrivateData";
  if ($async == true && !defined('PHP_WINDOWS_VERSION_MAJOR') &&
    !isRunning(fread(fopen('.refreshPid', 'r'), filesize(".refreshPid")))) {
    $command = sprintf("%s > %s 2>&1 & echo $! > %s", $command, "/dev/null", ".refreshPid");
  }
  return exec($command);
}

function buildAndCopyJar(){
  global $jarName;
  $jarPath = "working/$jarName";
  
  if (!file_exists("working")) { // If the working directory doesn't exist
    mkdir("working"); // Make the working directory
  }
  if (file_exists("build/libs/$jarName")) {
    if (!copy("build/libs/$jarName", $jarPath)) {
      error_log("Unable to copy and build jar!");
    }
  } else {
    if (file_exists("build.gradle")) {
      exec("./gradlew build");
      if (!copy("build/libs/$jarName", $jarPath)) {
        error_log("Unable to copy and build jar!");
      }
    }
  }
  chdir("working"); // Enter the working directory to run the jar / store sched.txt
}

function guidv4($data){ // Thanks https://stackoverflow.com/a/15875555/1709894
  assert(strlen($data) == 16);
  $data[6] = chr(ord($data[6]) & 0x0f | 0x40); // set version to 0100
  $data[8] = chr(ord($data[8]) & 0x3f | 0x80); // set bits 6-7 to 10
  return vsprintf('%s%s-%s-%s-%s-%s%s%s', str_split(bin2hex($data), 4));
}

function isRunning($pid){ // Thanks https://stackoverflow.com/a/45966/1709894
  try{
    $result = shell_exec(sprintf("ps %d", $pid));
    if(count(preg_split("/\n/", $result)) > 2){
      return true;
    }
  } catch(Exception $e){}
  
  return false;
}

main();
