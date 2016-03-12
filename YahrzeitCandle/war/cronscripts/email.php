#!/usr/bin/php5.5-cli

<?php
$hebmonths=array ("null","Nissan",
"Iyar", "Sivan", "Tamuz", "Av",
"Elul",
"Tishrei",
"Cheshvan",
"Kislev",
"Tevet",
"Shvat",
"Adar",
"Adar II");

require_once "AppConfig.class.php";
date_default_timezone_set("Asia/Jerusalem");


$fb = new Facebook\Facebook([
   'app_id'=>AppConfig::$appid,
   'app_secret'=>AppConfig::$appsecret]);

$fb->setDefaultAccessToken($fb->getApp()->getAccessToken());

$sql_q=" uid!=1234 and uid in (select uid from users where allow_email=1) and greg_date=date(from_unixtime(unix_timestamp()+86400))";
$sql="SELECT distinct uid as uid FROM yahrzeit where $sql_q";

$conn = get_db_conn();
$res=mysqli_query($conn,$sql);
while ($row = mysqli_fetch_assoc($res)) {
 $uids[]=$row['uid'];
}
$res=array();

if (!isset($uids)) exit;

$headers="From: Yahrzeit Candle <reminders@yahrzeitcandle.info>
Content-Type: text/plain; charset=us-ascii
Content-Disposition: inline";

$subject="Reminder email from Yahrzeit Candle";



foreach ($uids as $uid) {
 error_log("*********** $uid");
 try {
  $req="/$uid?fields=id,email";
  $request=$fb->request('GET',$req);
  $response=$fb->getClient()->sendRequest($request);
  error_log("id from api: " . $response->getGraphNode()->getField('id') );
  $to=$response->getGraphNode()->getField('email');
  if ($to==null) continue;
  $sql = "SELECT honoree,uid,greg_date from yahrzeit where uid=$uid and $sql_q";
  error_log($sql);
  $res=mysqli_query($conn,$sql);
  $greg_date=null;
  while ($row = mysqli_fetch_assoc($res)) {
   $hh[]=$row['honoree'];
   $greg_date=getdate(strtotime($row['greg_date']));
  }
  $res=array();
  $num_h=count($hh);
  $msg="This is an email reminder from Yahrzeit Candle that the ";
  switch ($num_h) {
   case 1:
    $msg.="Yahrzeit of ".$hh[0]." is";
    break;
 
   case 2:
    $msg.="Yahrzeits of ".$hh[0]." and ".$hh[1]." are";
    break;

   default:
    $msg.="Yahrzeits of ";
    for ($i=0;$i<$num_h-1;$i++) {
     $msg.=$hh[$i].", ";
    }
    $msg.="and ".$hh[$i]." are";
    break;
   }
  $msg.=" coming up, ";
  $msg.="starting the evening of ";
  $msg.=date("l, F j Y",$greg_date[0]);
  $msg.=".";
  if (!preg_match("/proxymail/","$to")) {
   $msg.="\n\nTo unsubscribe from notification emails from Yahrzeit Candle,
visit http://apps.facebook.com/yahrzeitcandle/ and deselect the emails option.";
  }
  error_log ("to: $to");
  error_log ("$msg");
  //0 for testing
  if (1) {
   mail  ( $to  , $subject  , $msg , $headers);
  } else { // set if stmt to 0 for testing
   print "to: $to\n";
   print "subject: $subject\n";
   print "$msg\n";
  }
 $hh=array();
 } catch (Exception $e) {
  error_log($e->getMessage());
 }
}

?>
