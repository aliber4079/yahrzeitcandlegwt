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

function calc_greg_date($y_hmon,$y_hebday) {
	$y_hmon=get_tishri_based_month_no($y_hmon);
$hebmonths=array (
 "",
"Nisan",
"Iyar", 
"Sivan",
"Tamuz", 
"Av",
"Elul",
"Tishrei",
"Cheshvan",
"Kislev",
"Tevet",
"Shvat",
"Adar1",
"Adar2",
""
);
date_default_timezone_set("Asia/Jerusalem");
$today=getdate();
$mon=$today['mon'];
$day=$today['mday'];
$year=$today['year'];

list ($hmon,$hebday,$hebyear)=explode("/",jdtojewish(gregoriantojd($mon, $day, $year)));

 $hebyear1 = 0;
    if (calc_month_offset($y_hmon) < calc_month_offset($hmon) )
       $hebyear1=$hebyear+1;
    else if (calc_month_offset($y_hmon) > calc_month_offset($hmon) )
       $hebyear1=$hebyear;
    else { // months equal
      if ($y_hebday>=$hebday) $hebyear1=$hebyear;
     else
      $hebyear1=$hebyear+1;
     }

	if ($y_hebday==30 && $y_hmon==8 && !long_cheshvan($hebyear1)) {
	 $y_hebday=1;
	 $y_hmon=9;
	} else if ($y_hebday==30 && $y_hmon==9 && short_kislev($hebyear1)) {
	 $y_hebday=29;
	 $y_hmon=9;
	}
	list($month, $day, $year) = explode('/',jdtogregorian(jewishtojd($y_hmon, $y_hebday, $hebyear1)));
    return sprintf('%d-%02d-%02d', $year,$month,$day);

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
global $facebook,$perms ;
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
    $greg_date=calc_greg_date($yahrzeit->{"date_month"},$yahrzeit->{"date_day"});

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
function set_perms($perms,$uid) {
global $facebook;
if (!isset($uid)) return NULL;
$lltoken=null;
error_log("last_visit was ".$perms['last_visit']." and time is ".time());
if (false && $perms['last_visit']<time()-86400){
 error_log ("setting extended access token because ".$perms['last_visit'].
	"< ".time()."-86400");
 $facebook->setExtendedAccessToken();
 $lltoken=$facebook->getAccessToken();
 error_log("tried to get lltoken: $lltoken");
}else {
    $conn = get_db_conn();
 list ($lltoken)=mysqli_fetch_row(mysqli_query($conn,"select lltoken from users where uid=$uid"));
 error_log("has logged in within the past day, not getting lltoken which was $lltoken");
}
$sql="replace into users values (".$uid.
   ",".intval($perms['allow_email']).",".
   intval($perms['allow_publish']).",unix_timestamp(),".
    ($lltoken ? "'$lltoken'" : "NULL").
	")";
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

function add_photo($id, $pid) {
if (!isset($pid)  || !isset($id)) 
	return (Array("method"=>"add_photo","status"=>"error"));
try {
    $conn = get_db_conn();
    $sql = "update yahrzeit set photo='$pid'  where id=$id";
    error_log($sql);
    $res = mysqli_query($conn,$sql);
    $sql = "select * from yahrzeit where id=$id";
    $res = mysqli_query($conn,$sql);
    $rows = mysqli_fetch_assoc($res);
	$rows['id']=intval($rows['id']);
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
    //return (Array("method"=>"clear_photo","response"=>"OK","yahrzeitlist"=>array($yahrzeit)));
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

?>