<?php

include "AppConfig.class.php";
error_log("remove: ".print_r($_REQUEST,1));


$s=parseSignedRequest($_POST['signed_request']);
error_log("signedReq: ".print_r($s,1));
$user_id = $s['user_id'];

$conn = get_db_conn();
    $sql = "delete FROM yahrzeit where uid=$user_id";
    error_log($sql);
    $res = mysqli_query($conn,$sql);
    $sql = "delete FROM users where uid=$user_id";
    error_log($sql);
    $res = mysqli_query($conn,$sql);


?>
