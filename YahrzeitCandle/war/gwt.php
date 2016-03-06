<?php

include_once "AppConfig.class.php";
$user_id=$psr="0";
if (isset($_FILES['photoUploader'])){
	$files=$_FILES['photoUploader'];
	error_log("files: " . print_r($files,1));
	$authResponse=json_decode(str_replace("\\","",$_REQUEST['authResponse']));
	$fbtoken=$authResponse->{'accessToken'};
	$fb=phpsdk($fbtoken);
	try {
		$data = [
				'message' => '',
				'source' => $fb->fileToUpload($files['tmp_name']),
				// Or you can provide a remote file location
				//'source' => $fb->fileToUpload('https://example.com/photo.jpg'),
		];
		
		$response = $fb->post('/me/photos', $data);
		$graphNode = $response->getGraphNode();
		$photo=null;
		if ($graphNode->getField("id")!=null /*&& $graphNode->getField("picture") != null */){
			$photo=new Photo($graphNode->getField("id") );
		}
		error_log("new photo from upload: " . print_r($photo,1));
		$yahrzeit=json_decode(str_replace("\\","",$_REQUEST['yahrzeit']));
		error_log("yahrzeit: ".print_r($yahrzeit,1));
		$exit_str=add_photo($yahrzeit->{'id'},$photo);
		error_log("exit str: ".json_encode($exit_str));
		exit(json_encode($exit_str));
		
	} catch(Facebook\Exceptions\FacebookResponseException $e) {
	  // When Graph returns an error
	  error_log('Graph returned an error: ' . $e->getMessage());
	  reauth($e);
	  exit;
	} catch(Facebook\Exceptions\FacebookSDKException $e) {
	  // When validation fails or other local issues
	  error_log('Facebook SDK returned an error: ' . $e->getMessage());
	  reauth($e);
	  exit;
	}
	
	exit();
}

$rawData = file_get_contents('php://input');
error_log("rawdata: ".$rawData);
$arr=json_decode($rawData);

if (!isset($arr->authResponse) 
		|| !isset($arr->authResponse->signedRequest) 
		|| (($psr=parseSignedRequest($arr->authResponse->signedRequest))==NULL) 
		|| ($user_id=$psr["user_id"])==0 
	){
 exit (json_encode(array("method"=>"error","status"=>"not_validated")));	
}
$conn=get_db_conn();


error_log("userid: $user_id");
if ($arr->{'method'}=="ping") {
	exit(json_encode(array(
	"method"=>"pong","status"=>"OK")));
} else
if ($arr->{'method'}=="add") {
	$yahrzeits=$arr->{"yahrzeitlist"};
	foreach ($yahrzeits as $yahrzeit) {
	$results[]=	add_yahrzeit($user_id,$yahrzeit);
	error_log("add result: ".print_r($results,1));
	   if (sizeof($results)==0) {
	        $j= "[{\"status\":\"error\"}]";
		error_log("response failed: $j");
	    }
	}
	$cnt=get_yahrzeit_count( $user_id);
	error_log("get yahrzeitcount for $user_id");
	$j=json_encode(Array("method"=>"add","status"=>"OK",
	 "yahrzeitlist"=>$results));
	 exit($j);
}
else if ($arr->{'method'}=="add_photo") {
	$json_array=add_photo($arr->{'yahrzeitlist'}[0]->{'id'},
	 $arr->{'yahrzeitlist'}[0]->{'photo'});
	error_log(print_r(json_encode($json_array),1));
	exit(json_encode($json_array));
}
else if ($arr->{'method'}=="clear_photo") {
	$json_array=clear_photo($arr->{'yahrzeitlist'}[0]);
	error_log(print_r(json_encode($json_array),1));
	exit(json_encode($json_array));
}
else if ($arr->{'method'}=="resync") {
  session_start();
  /*$_SESSION['fbtoken']= $arr->authResponse->accessToken;
  session_commit();*/
  $yahrzeits=get_yahrzeits($user_id);
  
  error_log("got yahrzeits");
  $userprefs=get_userprefs($user_id);
  $json_array=array("status"=>"OK", "method"=>"resync","userprefs"=>$userprefs,"yahrzeitlist"=>$yahrzeits);
  $j=json_encode($json_array);
  error_log("resync json: $j");
  exit($j);
}
else if ($arr->{'method'}=="delete") {
   $yahrzeits=$arr->{'yahrzeitlist'};
   foreach ($yahrzeits as $yahrzeit) {
     $res=delete_yahrzeit($user_id,$yahrzeit->{"id"});
	$cnt=get_yahrzeit_count( $user_id);
    if ($res!=null) {
         $j=json_encode(Array("status"=>"OK","method"=>"delete",
			"yahrzeitlist"=>array($yahrzeit)));
        error_log("delete json: $j");
        exit($j);
      }
   }
} else if ($arr->{'method'}=="modify") {
   $yahrzeits=$arr->{'yahrzeitlist'};
   foreach ($yahrzeits as $yahrzeit) {
    //error_log("modify item: ".$yahrzeit->{'id'});
     $res=modify_yahrzeit($user_id,$yahrzeit);
   if ($res!=null) {
         $j=json_encode(Array("status"=>"OK","method"=>"modify",
"yahrzeitlist"=>array($yahrzeit)));
        print ($j);
        //error_log("returning ".$j);
}
}
} else if($arr->{'method'}=="allow_email") {
if (!isset($arr->{'authResponse'})) {
error_log("in allow_email: no authResponse");
$j=json_encode(Array("status"=>"OK",'method'=>"allow_email", "allow_email"=>FALSE));
print ($j);
return null;
}
$allow_email=$arr->{'allow_email'} && TRUE;
$prefs=array("allow_email"=>$allow_email && TRUE);
   set_prefs($prefs,$user_id);
   $j=json_encode(array("status"=>"OK",'method'=>"allow_email", "allow_email"=>$allow_email));
	print ($j);
}

function reauth($e){
	error_log($e->getTraceAsString());
	unset ($_SESSION["fbtoken"]);
	exit(json_encode(array("method"=>"need_auth","status"=>"error")));	
}
?>
