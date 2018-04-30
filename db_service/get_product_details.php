<?php

/*
 * Following code will get single product details
 * A product is identified by product id (pid)
 */

// array for JSON response
$response = array();

// include db connect class
require_once __DIR__ . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

// check for post data
//if (true) {
if (isset($_GET["pid"])) {
    $pid = $_GET['pid'];
	//$pid = "666";

    // get a product from products table
    $tempresult = mysql_query("SELECT id_product, id_product_attribute FROM ps_product_attribute WHERE reference = $pid");

    if (!empty($tempresult)) {
        // check for empty result
        if (mysql_num_rows($tempresult) > 0) {
		
		
		

            $tempresult = mysql_fetch_array($tempresult);
			//---------------------------------------
			$tempprodid = $tempresult["id_product"];
			$productinfo = mysql_query("SELECT name, meta_title FROM ps_product_lang WHERE id_product = $tempprodid");
			$productinfo = mysql_fetch_array($productinfo);
			
            //---------------------------------------
			$tempprodatrid = $tempresult["id_product_attribute"];
			$resultattribute = mysql_query("SELECT id_attribute FROM ps_product_attribute_combination WHERE id_product_attribute = $tempprodatrid") or die(mysql_error());
			
			if (mysql_num_rows($resultattribute) > 0) {
			// looping through all results
			// products node
			$attributedesc = "";

			while ($row = mysql_fetch_array($resultattribute)) {
				// temp user array
				$tempatr = $row["id_attribute"];
				$tempattriname = mysql_query("SELECT name FROM ps_attribute_lang WHERE id_attribute=$tempatr");
				$tempattriname = mysql_fetch_array($tempattriname);
				$attributedesc = $attributedesc . " " . $tempattriname["name"];
			}
			//---------------------------------------
            $product = array();
            $product["pid"] = $pid;
            $product["name"] = $productinfo["name"];
			//$product["shortdesc"] = "in progress";
			$product["shortdesc"] = $productinfo["meta_title"];
            //$product["price"] = $result["price"];
            $product["description"] = $attributedesc;
            //$product["created_at"] = $result["created_at"];
            //$product["updated_at"] = $result["updated_at"];
			
			//Warehouses
			$warehouseresult = mysql_query("SELECT *FROM ps_warehouse") or die(mysql_error());
			$tempwarehouse = array();
			$response["warehouses"] = array();
			while ($row = mysql_fetch_array($warehouseresult)) {
				// temp user array
				
				$tempwarehouse["warehouseName"] = $row["name"];
				$tempwarehouse["warehouseID"] = $row["id_warehouse"];
				$idwarehousetemp = $row["id_warehouse"];
				$warehouseamountresult = mysql_query("SELECT physical_quantity FROM ps_stock WHERE id_product = $tempprodid AND id_product_attribute = $tempprodatrid AND id_warehouse = $idwarehousetemp");
				if(mysql_num_rows($warehouseamountresult)>0){
				$warehouseamountresult = mysql_fetch_array($warehouseamountresult);
				$tempwarehouse["warehouseAmount"] = $warehouseamountresult["physical_quantity"];
				}else{
					$tempwarehouse["warehouseAmount"] = "0";
				}

				// push single product into final response array
				array_push($response["warehouses"], $tempwarehouse);
			}	
			
            // success
            $response["success"] = 1;

            // user node
            $response["product"] = array();
            array_push($response["product"], $product);

            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "No product found";

            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No product found";

        // echo no users JSON
        echo json_encode($response);
    }
	}
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    // echoing JSON response
    echo json_encode($response);
}
?>