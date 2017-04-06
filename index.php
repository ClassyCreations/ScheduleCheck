<?php
$schedName = "sched.txt";
$jarName = "ScheduleCheck-1.0-SNAPSHOT.jar";
function main(){
    global $schedName;

    if (getenv('HEROKU')) buildAndCopyJar();
    if (!file_exists($schedName)) runAspenJar();

    $handle = fopen($schedName, "r");
    $contents = fread($handle, filesize($schedName));
    fclose($handle);

    $json = json_decode($contents);
    if (time() - $json->{'asOf'} > 120) {
        runAspenJar();
    }

    echo $contents;
}

function runAspenJar(){
    global $jarName;

    exec("java -jar $jarName -q -j -f sched.txt");
}

function buildAndCopyJar(){
    global $jarName;

    if (!file_exists("./$jarName") && file_exists("build/libs/$jarName")) {
        if (copy("build/libs/$jarName", "./$jarName")) {
            error_log("Unable to copy and build jar!");
        }
    } else {
        if (file_exists("build.gradle")) {
            exec("./gradlew build");
            if (copy("build/libs/$jarName", "./$jarName")) {
                error_log("Unable to copy and build jar!");
            }
        }
    }
}

main();