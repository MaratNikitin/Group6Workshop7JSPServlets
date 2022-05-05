<%--
  Created by IntelliJ IDEA.
  User: allan
  Date: 2022-04-20
  Time: 10:14 a.m.
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Add Package</title>
    <script   src="https://code.jquery.com/jquery-3.6.0.js"   integrity="sha256-H+K7U5CnXl1h5ywQfKtSj8PCmoN9aaq30gDh27Xc0jk="   crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/dayjs/1.11.1/dayjs.min.js"></script>
    <script>

        //function to add a specific package to db
        async function putPackage() {
            //format startdate and enddate
            var startdate = dayjs($("#PkgStartDate").val()).format("YYYY-MM-DD");
            var enddate = dayjs($("#PkgEndDate").val()).format("YYYY-MM-DD");

            //get the values of base price and commission
            var baseprice = $("#PkgBasePrice").val();
            var commission = $("#PkgAgencyCommission").val();

            //sets the textfields as visible or not based on validation
            if($("#PkgName").val().length > 50 || $("#PkgName").val().length == 0)
            {
                $("#lblPkgName").show();
            }
            else
            {
                $("#lblPkgName").hide();
            }

            if(startdate > enddate || startdate < Date.now() || $("#PkgStartDate").val() == "")
            {
                $("#lblPkgStartDate").show();
            }
            else
            {
                $("#lblPkgStartDate").hide();
            }

            if(startdate > enddate || enddate < Date.now() || $("#PkgEndDate").val() == "")
            {
                $("#lblPkgEndDate").show();
            }
            else
            {
                $("#lblPkgEndDate").hide();
            }

            if($("#PkgDesc").val().length > 50 || $("#PkgDesc").val().length == 0)
            {
                $("#lblPkgDesc").show();
            }
            else
            {
                $("#lblPkgDesc").hide();
            }

            if(baseprice <= commission || baseprice.length == 0)
            {
                $("#lblPkgBasePrice").show();
            }
            else
            {
                $("#lblPkgBasePrice").hide();
            }

            if(baseprice <= commission || commission.length == 0)
            {
                $("#lblPkgAgencyCommission").show();
            }
            else
            {
                $("#lblPkgAgencyCommission").hide();
            }

            //this function is to set the textfields is validated true or false
            var isPkgName = $("#PkgName").val().length > 50 || $("#PkgName").val().length == 0 ? false : true;
            var isPkgStartDate = startdate > enddate || startdate < Date.now() || $("#PkgStartDate").val() == "" ? false : true;
            var isPkgEndDate = startdate > enddate || enddate < Date.now() || $("#PkgEndDate").val() == "" ? false : true;
            var isPkgDesc = $("#PkgDesc").val().length > 50 || $("#PkgDesc").val().length == 0 ? false : true;
            var isPkgBasePrice = baseprice <= commission || baseprice.length == 0 ? false : true;
            var isPkgAgencyCommission = baseprice <= commission || commission.length == 0 ? false : true;

            console.log(isPkgStartDate);
            console.log(isPkgEndDate);
            //check if all textfields is validated, if false the alert message will show
            if(isPkgName == false
            || isPkgStartDate == false
            || isPkgEndDate == false
            || isPkgDesc == false
            || isPkgBasePrice == false
            || isPkgAgencyCommission == false
            )
            {
                //this is the alert message if validated false
                alert("Please check the validated textfields.");
            }
            else //if the textfields is validated true
            {
                //create json object for package details
                const putData = {
                    "packageId":0,
                    "pkgName":$("#PkgName").val(),
                    "pkgStartDate":startdate,
                    "pkgEndDate":enddate,
                    "pkgDesc":$("#PkgDesc").val(),
                    "pkgBasePrice":$("#PkgBasePrice").val(),
                    "pkgAgencyCommission":$("#PkgAgencyCommission").val()
                };

                //url for add packages from REST app
                var url = "http://localhost:8080/api/addpackage" ;
                try {
                    const response = await fetch(url,
                        {
                            method: "put",
                            headers: {"Content-type": "application/json"},
                            body: JSON.stringify(putData)
                        });

                    // check if the response is success or not
                    if (!response.ok) {
                        // failed message
                        const message = "Insert failed: status=" + response.status;
                        throw new Error(message);
                    }
                    else
                    {
                        // success message
                        alert("Successfully saved Package.");
                        // set the textfields as empty
                        $("#PkgName").val("");
                        $("#PkgStartDate").val("");
                        $("#PkgEndDate").val("");
                        $("#PkgDesc").val("");
                        $("#PkgBasePrice").val("");
                        $("#PkgAgencyCommission").val("");
                        window.location.reload();
                    }
                } catch (e) {
                    console.log("Error: " + e); //error logs
                }
            }

        }
    </script>
</head>
<%--adds view for header--%>
<header>
    <jsp:include page="header.jsp" />
</header>
<body>
<h1><%= "Travel Experts" %>
</h1>
<%--form for the package details fields--%>
<form>
    Id:<input id="PackageId" type="number" disabled="disabled" /><br />
    Package Name:<input id="PkgName" type="text" /><label id="lblPkgName" style="color: red;padding-left: 10px;">0 or >50 characters is not allowed.</label><br />
    Start Date:<input id="PkgStartDate" type="date" /><label id="lblPkgStartDate" style="color: red;padding-left: 10px;">The start date is required & must be less than end date.</label><br />
    End Date:<input id="PkgEndDate" type="date" /><label id="lblPkgEndDate" style="color: red;padding-left: 10px;">The end date is required & must be greater than start date.</label><br />
    Description:<input id="PkgDesc" type="text" /><label id="lblPkgDesc" style="color: red;padding-left: 10px;">0 or >50 characters is not allowed.</label><br />
    Base Price:<input id="PkgBasePrice" type="number" step=".01" /><label id="lblPkgBasePrice" style="color: red;padding-left: 10px;">Enter positive number value and base price must be greater than commission.</label><br />
    Commission:<input id="PkgAgencyCommission" type="number" step=".01" /><label id="lblPkgAgencyCommission" style="color: red;padding-left: 10px;">Enter positive number value and commission must be less than base price.</label><br />
    <button type="button" onclick="putPackage()">Insert</button>
</form>
<script>
    //when the document loads the alert labels is hide
    $(document).ready(function(){
        $("#lblPkgName").hide();
        $("#lblPkgStartDate").hide();
        $("#lblPkgEndDate").hide();
        $("#lblPkgDesc").hide();
        $("#lblPkgBasePrice").hide();
        $("#lblPkgAgencyCommission").hide();
    });
</script>
</body>
</html>
