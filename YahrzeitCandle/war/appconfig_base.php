<?php

function get_db_conn() {
    $conn = mysqli_connect(
      AppConfig::$db_ip,
      AppConfig::$db_user,
      AppConfig::$db_pass,
      AppConfig::$db_name
    );
    if (! $conn) {
      throw new Exception('Error connecting to database: ' . mysqli_error());
    }
    mysqli_query($conn,"SET NAMES 'utf8'");
    return $conn;
  }

function get_yahrzeit_count($user_id) {
    $conn = get_db_conn();
    $sql = "SELECT count(*) as cnt FROM yahrzeit WHERE uid = $user_id";
    error_log($sql);
    $res = mysqli_query($conn,$sql);
    $count = 0;
    while ($row = mysqli_fetch_assoc($res)) {
      $count = $row["cnt"];
    }
    return $count;
  }

function calc_greg_date($y_hmon,$y_hebday, $y_hebyear=null, $id=1) { //this is where yahrzeit date gets calculated
	//input: hebrew date
	// convert to gregdate with jdtogreg + jewishtojd
	// run gregdate thru hebcal
	//  hebcal -h $year -Y $tmpfile
	//output: greg date
	$y_hmon=get_tishri_based_month_no($y_hmon); //what jewishtojd expects

	$this_hebyear=$year=$month=$day=null;
	$today=explode("/",jdtojewish(unixtojd(time())));
	$this_hebyear=$today[2];
	$next_hebyear=$this_hebyear+1;
	if ($y_hebyear==null){
		//use current yr
		$y_hebyear=$this_hebyear;
	}
	list($month, $day, $year) = explode('/',jdtogregorian(jewishtojd($y_hmon, $y_hebday, $y_hebyear)));
	$output=hebcal_yahrzeit($month,$day,$year,$this_hebyear,$id);
	list ($returned_gregdate,$returned_id)=explode(" ", $output);
	
	//if $returned_gregdate prior to today...
	if (strtotime($returned_gregdate) < time()){
		$output=hebcal_yahrzeit($month,$day,$year,$next_hebyear,$id);
		list ($returned_gregdate,$returned_id)=explode(" ", $output);
	}
	
	
	if ($returned_id==$id){
		$year=$month=$day=null;
		list($month,$day,$year)=explode("/",$returned_gregdate);
		return sprintf('%d-%02d-%02d', $year,$month,$day);
	}
	return null;
}

function hebcal_yahrzeit($month,$day,$year,$this_hebyear,$id){
	$datestr=sprintf ("%d %d %4d",$month,$day,$year);
	$file_contents=$datestr . " " . $id . PHP_EOL ;
	$tmpfile= tempnam(sys_get_temp_dir(),"hebcal");
	file_put_contents($tmpfile, $file_contents);
	$result=null;
	$command=AppConfig::$hebcal_path  . " -h -H $this_hebyear -Y $tmpfile";
	$output=exec($command,$result);
	unlink($tmpfile);
	return $output;
}

function calc_month_offset($month) {
 return $month>6 ? $month-7 : $month+6 ;
}

function get_tishri_based_month_no($month){
	return (($month+6)%13+1);
}

function long_cheshvan( $year ){
  return ((days_in_heb_year ($year) % 10) == 5);
  }

function days_in_heb_year( $year ){
  return hebrew_elapsed_days ($year + 1) - hebrew_elapsed_days ($year);
  }
function short_kislev( $year ){
  return ((days_in_heb_year ($year) % 10) == 3);
  }


function hebrew_elapsed_days( $year ){

    $yearl = $year;
    $m_elapsed = (235 * intval (($yearl - 1) / 19) +
		    12 * (($yearl - 1) % 19) +
		    intval((((($yearl - 1) % 19) * 7) + 1) / 19));
   
    $p_elapsed = 204 + (793 * ($m_elapsed % 1080));
   
    $h_elapsed = (5 + (12 * $m_elapsed) +
		    793 * intval ($m_elapsed / 1080) +
		    intval($p_elapsed / 1080));
   
    $parts = ($p_elapsed % 1080) + 1080 * ($h_elapsed % 24);
   
    $day = 1 + 29 * $m_elapsed + intval($h_elapsed / 24);
    $alt_day;

   if (($parts >= 19440) ||
       ((2 == ($day % 7)) && ($parts >= 9924) && !(LEAP_YR_HEB ($year))) ||
       ((1 == ($day % 7)) && ($parts >= 16789) && LEAP_YR_HEB ($year - 1))){
    $alt_day = $day + 1;}
   else{
     $alt_day = $day;}
   
   if (($alt_day % 7) == 0 ||
       ($alt_day % 7) == 3 ||
       ($alt_day % 7) == 5){
      return $alt_day + 1;
   }
   else{
     return $alt_day;
  }
}

function LEAP_YR_HEB($yr){
 return ((1 + $yr[0]* 7) % 19 < 7 ?  true: false);
 }

function get_sql_q ($hmon, $hebday, $hebyear) {
if ($hmon==12 && !LEAP_YR_HEB($hebyear)) {
 $sql_q="((yahrzeit.date_month=12 and yahrzeit.date_day=$hebday) or (yahrzeit.date_month=13 and yahrzeit.date_day=$hebday))";
} else if ($hmon==8 && $hebday==29 && !long_cheshvan($hebyear)) {
 $sql_q="(yahrzeit.date_month=8 and (yahrzeit.date_day=29 or yahrzeit.date_day=30))";
} else if ($hmon==9 && $hebday==29 && short_kislev($hebyear) ) {
 $sql_q="(yahrzeit.date_month=9 and (yahrzeit.date_day=29 or yahrzeit.date_day=30))";
} else if (!LEAP_YR_HEB($hebyear) && $hmon==11 && $hebday==30) {
   //30 shvat exception
   $sql_q="((yahrzeit.date_month=11 and yahrzeit.date_day=30) or (yahrzeit.date_month=12 and yahrzeit.date_day=30))";
 } else 
 $sql_q="(yahrzeit.date_month=$hmon and yahrzeit.date_day=$hebday)";
 return $sql_q;
}

function get_yahrzeits($user_id) {
//global $facebook,$perms ;
$photos=get_photos($user_id);
	$conn = get_db_conn();
    $sql = "SELECT id, honoree,date_month,date_day,date_year,greg_date,photo  FROM yahrzeit
 where uid=$user_id order by greg_date asc";
error_log($sql);
$yahrzeits=array();
$res = mysqli_query($conn,$sql);
while ($row = mysqli_fetch_assoc($res)) {
      $row['date_day']=intval($row['date_day']);
      $row['date_month']=intval($row['date_month']);
      $row['id']=intval($row['id']);
      $row['date_year']=intval($row['date_year']);
      if ($row['photo']!=null){
      	if (count($photos)>0) {
      		foreach ($photos as $photo){
      			if ($photo->id==$row['photo']){
      				$row['photo']=array("id"=>$photo->id,"picture"=>$photo->picture);
      				break;
      			}
      		}
      	}
      }
      $yahrzeits[] = $row;
}

//error_log("perms from global variable: ".print_r($perms,1));
error_log("about to return yahrzeits");
    return $yahrzeits;
 } 


function delete_yahrzeit($user_id, $dbid) {
    $conn = get_db_conn();
    $sql = "delete from yahrzeit where uid=$user_id and id=$dbid";
    error_log($sql);
    $res = mysqli_query($conn,$sql);
  
    return $res;
}
function add_yahrzeit($user_id, $yahrzeit) {
    $conn = get_db_conn();
    $greg_date=calc_greg_date($yahrzeit->{"date_month"},$yahrzeit->{"date_day"},$yahrzeit->{"date_year"},$user_id);

	$sql="insert into yahrzeit (id, uid, honoree, date_month,date_day,date_year,".
    "greg_date) ".
   "values (null, $user_id,'".$yahrzeit->{"honoree"}."',".
       $yahrzeit->{"date_month"}.",".$yahrzeit->{"date_day"}.",".
       $yahrzeit->{"date_year"}.",'$greg_date')";

    error_log($sql);
    $res = mysqli_query($conn,$sql);
  $yahrzeit->{'id'}=mysqli_insert_id($conn); 
    return $yahrzeit;
}
function modify_yahrzeit($user_id, $yahrzeit) {
    $conn = get_db_conn();
    $greg_date=calc_greg_date($yahrzeit->{"date_month"},
          $yahrzeit->{"date_day"});
 $sql="update yahrzeit set honoree='".$yahrzeit->{"honoree"}."',".
       "date_month=".$yahrzeit->{"date_month"}.",".
       "date_day=".$yahrzeit->{"date_day"}.",".
	"date_year=".$yahrzeit->{"date_year"}.", greg_date='$greg_date' ".
        "where uid=$user_id and id=".$yahrzeit->{"id"};

    error_log($sql);
    $res = mysqli_query($conn,$sql);
  $yahrzeit->{'greg_date'}=$greg_date;
    return $yahrzeit;
}
function set_prefs($prefs,$uid) {
global $facebook;
if (!isset($uid)) return NULL;
$conn = get_db_conn();
$sql="replace into users values ($uid,".
		intval($prefs['allow_email']).
	",0,unix_timestamp(),NULL)";
    error_log("sql: $sql");
    $conn = get_db_conn();
    $res = mysqli_query($conn,$sql);
	return $res;
}
function get_userprefs($user_id) {
    $conn = get_db_conn();
$sql="select * from users where uid=$user_id";
    $res = mysqli_query($conn,$sql);
if (!$res) {
	error_log($conn->error);
	return 0;
}
    return mysqli_fetch_assoc($res);
	 
}

function get_photos ($uid){
	global $arr;
	$fbtoken=$arr->authResponse->accessToken;
	$fb=phpsdk($fbtoken);
	 
	$conn = get_db_conn();
	//$sql="select GROUP_CONCAT(distinct photo) as photos from yahrzeit where uid=$uid";
	$sql="select distinct photo from yahrzeit where uid=$uid and photo is not null";
	$res = mysqli_query($conn,$sql);
	if (!$res) {
		error_log($conn->error);
		return 0;
	}
	$batch=$photos=null;
	$i=0;
	while ($photo=mysqli_fetch_assoc($res)['photo']) {
	 $photos[$i]=$photo;
	 $batch[]=$fb->request('GET',"/" . $photo . "?fields=id,picture.type(normal)");
	 $i++;
	}
	if (count($photos)==0){return null;}
error_log(print_r($photos,1));
	//validate photos.
	try {
		$response = $fb->sendBatchRequest($batch);
		$graphNode = $response->getGraphNode();
		$photos_to_delete=$photos_to_display=null;
		foreach ($graphNode as $key => $value) {
			$value=json_decode($value);
			error_log("code: " . $value->code);
			error_log($value->body);
		if ($value->code==200) {
				$photos_to_display[]=json_decode($value->body);
			} else  {
				error_log("delete " . $photos[$key]);
				$photos_to_delete[]=$photos[$key];
			}
		}
		error_log(print_r($photos_to_display,1));
		if (count($photos_to_delete)>0){
			$sql="update yahrzeit set photo=null where uid=$uid and photo in ('" .
			implode("','",$photos_to_delete) . "')";
			error_log($sql);
			mysqli_query($conn,$sql);		
		}
		if (count($photos_to_display)>0) {
			return $photos_to_display;
		}
		return null;
		
	} catch(FacebookRequestException $e) {
		// error handling
		return($e->getMessage());
	}
	
	//error_log(print_r(array_column($node->asArray(),'body'),1));
	
}

function validate_session ($sess) {
foreach ($sess as $key => $value) {
            $session[$key] = $value;
        }



      $session_without_sig = $session;
      unset($session_without_sig['sig']);
      $expected_sig = generateSignature(
        $session_without_sig,
        AppConfig::$appsecret);

#print ("received $expected_sig\n");
#print ("expected: ".$session['sig']."\n");

return  $expected_sig==$session['sig'];

}
function generateSignature($params, $secret) {
    // work with sorted data
    ksort($params);

    // generate the base string
    $base_string = '';
    foreach($params as $key => $value) {
      $base_string .= $key . '=' . $value;
    }
    $base_string .= $secret;

    return md5($base_string);
  }

function add_photo($id, $photo) {
	global $arr,$fbtoken,$fb;
	if (!isset($photo)  || !isset($id)) 
	return (Array("method"=>"add_photo","status"=>"error"));
try {
	if (!isset($fbtoken)){
	   $fbtoken=$arr->authResponse->accessToken;
	   $fb=phpsdk($fbtoken);
	}
	//$response=$fb->get("/".$pid->id."?fields=id,picture.type(normal)");
	error_log(print_r($photo,1));
	$apireq= "/" . $photo->id . "?fields=id,picture.type(normal)";
	error_log("apireq: " . $apireq);
	$request=$fb->request('GET', $apireq );
	$response=$fb->getClient()->sendRequest($request);
	$graphNode = $response->getGraphNode();
	
	if ($graphNode->getField("id")==$photo->id && $graphNode->getField("picture") != null ){
		$photo->picture=$graphNode->getField("picture");
	}
    $conn = get_db_conn();
    $sql = "update yahrzeit set photo='$photo->id'  where id=$id";
    error_log($sql);
    $res = mysqli_query($conn,$sql);
    $sql = "select * from yahrzeit where id=$id";
    $res = mysqli_query($conn,$sql);
    $rows = mysqli_fetch_assoc($res);
	$rows['id']=intval($rows['id']);
	$rows['photo']=$photo;
    return (Array("method"=>"add_photo","status"=>"OK","yahrzeitlist"=>array($rows)));
  } catch (Exception $e) {
    error_log("exception $e");
	return (Array("method"=>"add_photo","status"=>"error"));
  }
}
function clear_photo($yahrzeit) {
error_log(print_r($yahrzeit,1));
    $conn = get_db_conn();
    $sql = "update yahrzeit set photo=NULL  where id=".$yahrzeit->{'id'};
    error_log($sql);
    $res = mysqli_query($conn,$sql);
	$y_temp=array('id'=>$yahrzeit->{'id'},'honoree'=>$yahrzeit->{'honoree'});
    return (Array("method"=>"clear_photo","status"=>"OK","yahrzeitlist"=>array($y_temp)));
}


function get_access_token() {

$ch = curl_init();
$url="https://graph.facebook.com/oauth/access_token?type=client_cred&client_id=".AppConfig::$appid."&client_secret=".AppConfig::$appsecret;
$opts = array(
    CURLOPT_CONNECTTIMEOUT => 10,
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_TIMEOUT        => 60,
    CURLOPT_URL => $url,

);

curl_setopt_array($ch, $opts);

$arr=explode("=",curl_exec($ch));
$access_token=$arr[1];
$errno=curl_errno($ch);
curl_close($ch);
if ($errno!=0){
 error_log("curl had a problem: $errno");
 return NULL;
 }
return $access_token;

}

function exitException($e){
	header("Content-type: text/plain");
	print $e->getMessage() ;
	
	exit($e->getTraceAsString()); //$e->getMessage() . " at " . $e->getFile() . " Line: " . $e->getLine() );

}

function parseSignedRequest($signed_request) {
	list($encoded_sig, $payload) = explode('.', $signed_request, 2);

	$secret = AppConfig::$appsecret;

	// decode the data
	$sig = base64_url_decode($encoded_sig);
	$data = json_decode(base64_url_decode($payload), true);

	// confirm the signature
	$expected_sig = hash_hmac('sha256', $payload, $secret, $raw = true);
	if ($sig !== $expected_sig) {
		error_log('Bad Signed JSON signature!');
		return null;
	}

	return $data;
}

function base64_url_decode($input) {
	return base64_decode(strtr($input, '-_', '+/'));
}

function phpsdk($fbtoken){
	$fb = new Facebook\Facebook([
			'app_id' => AppConfig::$appid,
			'app_secret' => AppConfig::$appsecret,
			'default_graph_version' => 'v2.5',
			'cookie' => false,
			'default_access_token' => $fbtoken,
	
	]);
	return $fb;
}
class Photo {
	public $picture;
	public $id;
  function __construct($id,$picture=null){
  	$this->id=$id;
  	$this->picture=$picture;
 }
}


?>
