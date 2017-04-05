<?php
$filename = "sched.txt";
$handle = fopen($filename, "r");
$contents = fread($handle, filesize($filename));
fclose($handle);

$json = json_decode($contents);
if (time() - $json->{'asOf'} > 120) {
    exec("java -jar ScheduleCheck-1.0-SNAPSHOT.jar -q -j -f sched.txt");
}

echo $contents;