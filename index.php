<?php
$schedName = "sched.txt";
$jarName = "ScheduleCheck-1.0-SNAPSHOT.jar";
function main(){
    global $schedName;
    buildAndCopyJar();

    $uname = $_POST['username'];
    $pass = $_POST['password'];

    $json = json_decode(getCachedSched());
    if (!$uname == null && !$pass == null){
        echo runAspenJar($uname, $pass, "/dev/null", false);
    } else if (time() - $json->{'asOf'} > 120) {
        runAspenJar(getenv('ASPEN_UNAME'), getenv('ASPEN_PASS'), $schedName, true); // Schedule File refresh
        echo getCachedSched();
    } else {
        echo getCachedSched();
    }


}

function getCachedSched(){
    global $schedName;
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
 * @return mixed
 */
function runAspenJar($username, $pass, $file, $async){
    global $jarName;

    $command = "java -jar $jarName -j -f $file -u $username -p $pass";
    if ($async == true && !defined('PHP_WINDOWS_VERSION_MAJOR')){
        return exec($command . " &> /dev/null &");
    } else {
        return exec($command . " --hidePrivateData");
    }
}

function buildAndCopyJar(){
    global $jarName;

    if (file_exists("build/libs/$jarName")) {
        if (!copy("build/libs/$jarName", "$jarName")) {
            error_log("Unable to copy and build jar!");
        }
    } else {
        if (file_exists("build.gradle")) {
            exec("./gradlew build");
            if (!copy("build/libs/$jarName", "$jarName")) {
                error_log("Unable to copy and build jar!");
            }
        }
    }
}

main();