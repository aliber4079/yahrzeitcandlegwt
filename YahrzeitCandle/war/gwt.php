<?php
//exit(print_r($_SESSION,1));

include_once "AppConfig.class.php";

session_start();
error_log(print_r($_SESSION,1));
error_log("printing cookie...");
error_log("cookie: ".print_r($_COOKIE,1));
error_log("printed cookie...");
$conn=get_db_conn();
$user_id="0";
$facebook = new Facebook\Facebook([
		'app_id' =>  AppConfig::$appid ,
		'app_secret' => AppConfig::$appsecret ,
		'default_graph_version' => 'v2.5',
		'cookie' => true,
		'domain' => AppConfig::$domain
]);
$rawData = file_get_contents('php://input'); 
error_log("rawdata: ".$rawData);
$arr=json_decode($rawData);


$jshelper = $facebook->getJavaScriptHelper();
$t=$me=null;
if (isset($_SESSION['fbtoken'])){
	$facebook->setDefaultAccessToken($_SESSION['fbtoken']);
}else if (isset($arr->{'access_token'}) && ($arr->{'access_token'}!==null)){
	$facebook->setDefaultAccessToken($arr->{'access_token'});
	error_log("set access token from payload: " . $arr->{'access_token'});
} else {//no token in php session cookie yet, try to get from fb cookie
	try{
		$accessToken=$jshelper->getAccessToken();
		$facebook->setDefaultAccessToken($accessToken);
		$_SESSION['fbtoken']=$accessToken;
		session_commit();
	} catch(Facebook\Exceptions\FacebookResponseException $e){
		//need to re-authenticate
		exit(json_encode(array("method"=>"need_auth","status"=>"error")));
		//"method"=>"add","response"=>"OK",
		exitException($e);
	} catch(Exception $e){
		exitException($e);
	}
}


try {
	$me=$facebook->get('/me/');

} catch (Facebook\Exceptions\FacebookAuthorizationException $e){
	unset($_SESSION['fbtoken']);
	exit(json_encode(array(array("method"=>"need_auth","response"=>"error"))));
	exitException($e);
} catch(Facebook\Exceptions\FacebookSDKException $e){
	unset($_SESSION['fbtoken']);
	exit(json_encode(array(array("method"=>"need_auth","response"=>"error"))));
	exitException($e);
}

$user_id=$me->getGraphUser()->getField('id');


error_log(print_r($me,1));


if (isset($_FILES["photoUploader"])){
 $files=$_FILES['photoUploader'];
 if (!empty($files)){
  error_log("request: ".print_r($_REQUEST,1));
  error_log("photo uploader ".print_r($files,1));
  if ($files['error']==2){
  exit("file was too large");
 }
}
 
$authResponse=json_decode(str_replace("\\","",$_REQUEST['authResponse']));
$yahrzeit=json_decode(str_replace("\\","",$_REQUEST['yahrzeit']));
error_log("yahrzeit: ".print_r($yahrzeit,1));
$access_token=null;
if (isset($authResponse->{'accessToken'})){
	$access_token=$authResponse->{'accessToken'};
}
$user=$facebook->getUser();
error_log("user: ".print_r($user,1));
error_log("access_token: $access_token");
if(defined('DEV_MODE')){
 $facebook->setAccessToken($access_token);
}


$uid=$authResponse->{'userID'};
$result=null;
try {

error_log("files: ".print_r($files,1));
error_log("accesstoken: ".$facebook->getAccessToken());
$photo_data= array("source"=>strtolower($files['name']),
	"file"=>"@".$files['tmp_name'],
	"message"=>"",
	"access_token"=>$facebook->getAccessToken(),
	"fileUpload"=>true);

$result=$facebook->api("/$uid/photos", "POST",$photo_data);

$row_id=$yahrzeit->{'id'};
$pid=$result['id'];
error_log("result: ".print_r($result,1));
error_log("result: ".print_r($photo_data,1));
error_log("pid: $pid");


$result=$facebook->api("/$pid");
error_log("result: ".print_r($result,1));
error_log("calling add photo with arguments: ".$pid.", ".$result['images'][3]['source'].", ".$result['images'][0]['source'].", ".$row_id);

$exit_str=add_photo($pid,$result['images'][3]['source'], $result['images'][0]['source'], $row_id);
error_log("exit str: ".json_encode($exit_str));
exit(json_encode($exit_str));


} catch (Exception $e) {error_log("exception: ".$e);exit(0); }
} //end if files

error_log("arr: ".print_r($arr,1));
// $arr->{'authResponse'} && $user_id=$arr->{'authResponse'}->{'userID'};
//$user_id=$facebook->getUser();
error_log("setting userid to $user_id");
if (!$user_id || $user_id==0) {
 error_log("oops, bailing");
 exit(json_encode(array(array("response"=>"error"))));
}
error_log("userid: $user_id");
if ($arr->{'method'}=="ping") {
	exit(json_encode(array(
	"method"=>"pong","status"=>"OK")));
} else
if ($arr->{'method'}=="add") {
   $yahrzeits=$arr->{"yahrzeitlist"};
 //error_log("length of yahrzeits is ".sizeof($yahrzeits));
   foreach ($yahrzeits as $yahrzeit) {
$results[]=	add_yahrzeit($user_id,$yahrzeit);
error_log("add result: ".print_r($results,1));
      if (sizeof($results)==0) {
        $j= "[{\"response\":\"failed\"}]";
	error_log("response failed: $j");
	
    }
}

$cnt=get_yahrzeit_count( $user_id);
  error_log("get yahrzeitcount for $user_id");
  $j=json_encode(Array(Array("method"=>"add","response"=>"OK",
"count"=>intval($cnt), "yahrzeitlist"=>$results)));
  print $j;

}
 else if ($arr->{'method'}=="add_photo") {
$json_array=add_photo($arr->{'yahrzeitlist'}[0]->{'id'});
error_log(print_r(json_encode(array($json_array)),1));
exit(json_encode(array($json_array)));
}
else if ($arr->{'method'}=="clear_photo") {
$json_array=clear_photo($arr->{'yahrzeitlist'}[0]);
error_log(print_r(json_encode(array($json_array)),1));
exit(json_encode(array($json_array)));

} else if ($arr->{'method'}=="resync") {
  $yahrzeits=get_yahrzeits($user_id);
  error_log("got yahrzeits");
  $userprefs=get_userprefs($user_id);
  $json_array=array("status"=>"OK", "method"=>"resync","userprefs"=>$userprefs,"yahrzeitlist"=>$yahrzeits);
  $j=json_encode($json_array);
  error_log("resync json: $j");
  exit($j);

} else if ($arr->{'method'}=="delete") {
   $yahrzeits=$arr->{'yahrzeitlist'};
   foreach ($yahrzeits as $yahrzeit) {
     $res=delete_yahrzeit($user_id,$yahrzeit->{"id"});
	$cnt=get_yahrzeit_count( $user_id);
    if ($res!=null) {
         $j=json_encode(Array(Array("response"=>"OK","method"=>"delete",
			"count"=>intval($cnt),"yahrzeitlist"=>array($yahrzeit))));
        print ($j);
        error_log("delete json: $j");
      }
   }
} else if ($arr->{'method'}=="modify") {
   $yahrzeits=$arr->{'yahrzeitlist'};
   foreach ($yahrzeits as $yahrzeit) {
    //error_log("modify item: ".$yahrzeit->{'id'});
     $res=modify_yahrzeit($user_id,$yahrzeit);
   if ($res!=null) {
         $j=json_encode(Array(Array("response"=>"OK","method"=>"modify",
"yahrzeitlist"=>array($yahrzeit))));
        print ($j);
        //error_log("returning ".$j);
}
}
} else if($arr->{'method'}=="allow_email") {
if (!isset($arr->{'authResponse'})) {
error_log("in allow_email: no authResponse");
$j=json_encode(Array(Array("response"=>"OK",'method'=>"allow_email", "allow_email"=>FALSE)));
print ($j);
return null;
}
$allow_email=$arr->{'allow_email'} && TRUE;
$oldperms=get_perms($user_id);
$allow_publish=$oldperms['allow_publish'] && TRUE;
$perms=array("allow_email"=>$allow_email && TRUE);
$perms['allow_publish']=$allow_publish && TRUE;
   set_perms($perms,$user_id);
   $j=json_encode(Array(Array("response"=>"OK",'method'=>"allow_email", "allow_email"=>$allow_email)));
print ($j);

}

 else if($arr->{'method'}=="allow_publish") {
error_log(print_r($arr,1));
if (!isset ($arr->{'authResponse'})) {
error_log("in allow_publish: no authResponse");
   $j=json_encode(Array(Array("response"=>"OK",'method'=>"allow_publish", "allow_publish"=>FALSE)));
print ($j);
//error_log(print_r($j,1));
return null;
}
   $allow_publish=$arr->{'allow_publish'} && TRUE;
$perms=array("allow_publish"=>$allow_publish);
$oldperms=get_perms($user_id);

$allow_email=$oldperms['allow_email'] && TRUE;
$perms['allow_email']=$allow_email;


   set_perms($perms,$user_id);
   $j=json_encode(Array(Array("response"=>"OK",'method'=>"allow_publish", "allow_publish"=>$allow_publish)));
print ($j);
error_log(print_r($j,1));

}

?>
