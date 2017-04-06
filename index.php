<?php
$schedName = "sched.txt";
$jarName = "ScheduleCheck-1.0-SNAPSHOT.jar";
function main(){
    global $schedName;

    buildAndCopyJar();
    if (!file_exists($schedName)) runAspenJar();

    $json = json_decode(getSched());
    if (time() - $json->{'asOf'} > 120) {
        runAspenJar();
    }

    echo getSched();
}

function getSched(){
    global $schedName;
    $handle = fopen($schedName, "r");
    $contents = fread($handle, filesize($schedName));
    fclose($handle);
    return $contents;
}

function runAspenJar(){
    global $jarName;

    exec("java -jar $jarName -q -j -f sched.txt -u " . getenv("ASPEN_UNAME") . " -p " . getenv("ASPEN_PASS"), $output);
    error_log(implode(",", $output));
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