import { initializeApp } from "https://www.gstatic.com/firebasejs/9.8.1/firebase-app.js";
import { getAnalytics } from "https://www.gstatic.com/firebasejs/9.8.1/firebase-analytics.js";
import { getAuth, createUserWithEmailAndPassword, signInWithEmailAndPassword, sendEmailVerification, sendPasswordResetEmail, signOut, onAuthStateChanged} from "https://www.gstatic.com/firebasejs/9.8.1/firebase-auth.js";
const firebaseConfig = {
    apiKey: "AIzaSyDI5h-Mt9fnxdilu4vf1eeQXyJlBnK4Kv0",
    authDomain: "vnscollegeattendance.firebaseapp.com",
    databaseURL: "https://vnscollegeattendance-default-rtdb.firebaseio.com",
    projectId: "vnscollegeattendance",
    storageBucket: "vnscollegeattendance.appspot.com",
    messagingSenderId: "1282468278",
    appId: "1:1282468278:web:0911f0b1d3f5670569b917",
    measurementId: "G-295J3JSG35"
  };
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
import { getDatabase, ref, set, child, get, update, remove, onValue, query, orderByChild} from "https://www.gstatic.com/firebasejs/9.8.1/firebase-database.js";
const db = getDatabase();
const auth = getAuth();
let userid,classvalue,classval;
let usernam;
let qrsize,qrtime;
const rname = document.getElementById("rname");
const remail = document.getElementById("remail");
const rphone = document.getElementById("rphone");
const rpassword = document.getElementById("rpassword");
const lemail = document.getElementById("lemail");
const lpassword = document.getElementById("lpassword");
const registerbtn = document.getElementById("registerbtn");
const loginbtn = document.getElementById("loginbtn");
const forgetpasswordbtn = document.getElementById("forgetpassword");
const addClassTab = document.querySelector(".addclassesBar");
const qrGeneratorTab = document.querySelector(".qrgenerateBar");
const login_register =  document.getElementById("login-regsiter");
const dashboard = document.getElementById("dashboard");
const tbody = document.getElementById("tbody1");let mont = ["January","February","March","April","May","June","July","August","September","October","November","December"];
function formDate(val){
    if(val>=0 && val<=9){
        val="0"+val;
    }
    return val;
}
let months = {
        "01" : "January",
        "02" : "February",
        "03" : "March",
        "04" : "April",
        "05" : "May",
        "06" : "June",
        "07" : "July",
        "08" : "August",
        "09" : "September",
        "10" : "October",
        "11" : "November",
        "12" : "December"
}
var dVal,yVal,mVal;
function validate(){
    let nameregex = /^[a-zA-z0-9\s]{5,}$/;
    let emailregex = /(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))/;
    let passwordregex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{6,16}$/;
    let phoneregex = /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/;
    if(!nameregex.test(rname.value)){
        alert("Username can be alphanumeric with minimum lenght of 5\nNo special characters allowed.");
        return false;
    }
    if(!emailregex.test(remail.value)){
        console.log(remail.value);
        alert("Enter a valid email");
        return false;
    }
    if(!phoneregex.test(rphone.value)){
        alert("Enter a valid phone number");
        return false;
    }
    if(!passwordregex.test(rpassword.value)){
        alert("Password must conatain : \n  Atleast one lowercase alphabet\n  Atleast one uppercase alphabet\n  Atleast one digit\n  Password must be minimum of 6 and maximum of 16\n  Atleast one special character (!@#$%^&*)");
        return false;
    }
    return true;
} 
function RegisterUser(){
    if(!validate()){
        return;
    }
    document.getElementById("circle").style.display = "initial";
    createUserWithEmailAndPassword(auth, remail.value, rpassword.value)
    .then((userCredential) => {
        // Signed in 
        const user = userCredential.user;
        set(ref(db,"Admin/"+user.uid),{
            username: rname.value,
            email: remail.value,
            phone: rphone.value
        })
        document.getElementById("circle").style.display = "none";
        alert("User created succesfully");
        sendEmailVerification(auth.currentUser)
        .then(() => {
            alert("Email verification link sent\nVerify your email and login to continue\nIf email not found check in spam");
        });
    })
    .catch((error) => {
        if(error.code == "auth/email-already-in-use"){
            alert("You have already registered");
        }
        document.getElementById("circle").style.display = "none";
    });
}
registerbtn.addEventListener('click',RegisterUser);
function SigninUser(){
    if(lemail.value==""){
        alert("Enter email to login");
        return;
    }
    if(lpassword.value==""){
        alert("Enter password to login");
        return;
    }
    document.getElementById("circle").style.display = "initial";
    signInWithEmailAndPassword(auth, lemail.value, lpassword.value)
    .then((userCredential) => {
        const dt = new Date();
        const user = userCredential.user;
        userid = userCredential.user.uid;
        console.log(userid);
        // userid = "uGapx3sGCDVMHMS34dsbZrINrSB2";
        update(ref(db,"Admin/"+user.uid),{
            last_login: dt
        })
        document.getElementById("circle").style.display = "none";
        if(auth.currentUser.emailVerified==false){
            alert("Verify email to login");
        }
        else{
            login_register.style.display = "none";
            dashboard.style.display = "initial";
            addClassTab.style.display="initial";
            qrGeneratorTab.style.display="none";
            setTimeout(function(){
                alert("Succesfully Logged in");
            },10);
            GetAllCoursesRealTime();
            const dbRefer = ref(db);
            get(child(dbRefer,"Admin/"+userid)).then((snap)=>{
                if(snap.exists())
                    usernam = snap.val().username;
            });
        }
    })
    .catch((error) => {
        document.getElementById("circle").style.display = "none";
        if(error.code=="auth/wrong-password"){
            alert("Incorrect Password");
        }
        if(error.code=="auth/user-not-found"){
            alert("Account doesnt exists with this email");
        }
        
    });
}
loginbtn.addEventListener('click',SigninUser);
document.getElementById("dashLogout").addEventListener('click',function(){
    const auth = getAuth();
    signOut(auth).then(() => {
        login_register.style.display = "initial";
        dashboard.style.display = "none";
        setTimeout(function(){
            alert("Succesfully logged out");
        },10);
    }).catch((error) => {
        alert("Error!! Unable to logout the user");
    });
    stopQrTable();
})
forgetpasswordbtn.addEventListener('click',function(){
    sendPasswordResetEmail(auth, lemail.value)
    .then(() => {
        alert("Password Reset Email Sent");
    })
    .catch((error) => {
        if(error.code=="auth/missing-email"){
            alert("Eneter the email to send password reset link");
        }
        if(error.code=="auth/user-not-found"){
            alert("Account doesnt exists with this email");
        }
    });
})
function stopQrTable(){
    document.getElementById("studaTab").style.display = "none";
    document.getElementById("selectdate").style.display = "initial";
    remove(ref(db,"Admin/"+userid+"/"+"qr"))
    clearInterval(myInterval);
    document.querySelector(".output").style.display = 'none';
    document.querySelector(".qroptions").style.display = "initial";
    qrbtn.style.display = 'initial';
    qrstopbtn.style.display = 'none';
}
document.getElementById("dashHome").addEventListener('click',function(){
    addClassTab.style.display = "initial";
    qrGeneratorTab.style.display = "none";
    stopQrTable();
})
const addBox = document.querySelector(".add-box");
const addContainer = document.querySelector(".class-wrapper");
const popupBox = document.querySelector(".popup-box"),
popupTitle = popupBox.querySelector("header p"),
closeIcon = popupBox.querySelector("header i"),
titleTag = popupBox.querySelector("input"),
addBtn = popupBox.querySelector("button");
addBox.addEventListener("click", () => {
    popupTitle.innerText = "Create a new class";
    addBtn.innerText = "Create";
    popupBox.classList.add("show");
    document.querySelector("body").style.overflow = "hidden";
    if(window.innerWidth > 660) titleTag.focus();
});
closeIcon.addEventListener("click", () => {
    titleTag.value = "";
    popupBox.classList.remove("show");
    document.querySelector("body").style.overflow = "auto";
});
let title;
addBtn.addEventListener("click", e => {
    e.preventDefault();
    title = titleTag.value.trim();
    console.log(title);
    let classregex = /^[a-zA-z0-9-_!@#$*]{1,15}$/;
    if(!classregex.test(title)){
        alert("Classname should not contain whitespaces and maximum length is 15\nClassname can contain alphanumeric and special chracters -_!@#$*");
        return;
    }
    if (confirm("Are you sure you want to create class ?") == true) {
        createClass(title);
    }
    closeIcon.click();
});
function createClass(className){
    let d = new Date();
    let yearVal = d.getFullYear();
    let monthVal = mont[d.getMonth()];
    let dateVal = d.getDate();
    let hourVal = formDate(d.getHours());
    let minuteVal = formDate(d.getMinutes());
    let secVal = formDate(d.getSeconds());
    set(ref(db,"Admin/"+userid+"/Classes/"+className),{
        Classname : className,
        Date : `${dateVal} ${monthVal} ${yearVal} ${hourVal}:${minuteVal}:${secVal}`
    })
}
function AddItemToContainer(value){
    let li = document.createElement("li");
    li.innerHTML=value;
    li.classList.add("text");
    li.addEventListener('click',qrFunction);
    addContainer.append(li);
}
function AddCoursesToList(course){
    course.forEach(element => {
        AddItemToContainer(element.Classname);
    });
}
function GetAllCoursesRealTime(){
    const dbRef = ref(db,"Admin/"+userid+"/Classes");
    onValue(dbRef,(snapshot)=>{
        var courses = [];
        addContainer.innerHTML = "";
        snapshot.forEach(childSnapshot => {
            courses.push(childSnapshot.val());
        });
        AddCoursesToList(courses);
    });
}
function qrFunction(){
    classvalue="Class : "+this.innerHTML;
    classval = this.innerHTML;
    document.getElementById("classnameh").innerHTML=classvalue;
    addClassTab.style.display="none";
    qrGeneratorTab.style.display="initial";
}
document.getElementById("datesubmit").addEventListener('click',function(){
    let date = document.getElementById("form-date").value;
    if(date==""){
        alert("Nothing to Display.  Select the Date");
    }
    else{
        dVal = date;
        yVal = date.slice(0,4);
        mVal = months[date.slice(5,7)];
        document.getElementById("studaTab").style.display = "initial";
        document.getElementById("selectdate").style.display = "none";
        GetAllDataRealTime();
    }
})
document.getElementById("back").addEventListener('click',function(){
    document.getElementById("studaTab").style.display = "none";
    document.getElementById("selectdate").style.display = "initial";

})
function AddItemToTable(roll,name,time,date){
    let trow = document.createElement("tr");
    let td1 = document.createElement('td');
    let td2 = document.createElement('td');
    let td3 = document.createElement('td');
    let td4 = document.createElement('td');
    let td5 = document.createElement('td');

    td1.innerHTML = ++stdNo;
    td2.innerHTML = roll;
    td3.innerHTML = name;
    td4.innerHTML = time;
    td5.innerHTML = date;

    trow.appendChild(td1);
    trow.appendChild(td2);
    trow.appendChild(td3);
    trow.appendChild(td4);
    trow.appendChild(td5);

    tbody.appendChild(trow);
    trow.style.textAlign = "center";
    trow.style.border = "1.5px solid red";
}
let stdNo=0;
function GetAllDataRealTime(){
    const dbRef = query(ref(db,"users/"),orderByChild("rollnumber"));
    onValue(dbRef,(snapshot)=>{
        stdNo = 0;
        tbody.innerHTML = "";
        snapshot.forEach(childSnapshot => {
            const dbRef2 = ref(db,"users/"+childSnapshot.val().uid+"/Attendance/"+usernam+"/"+classval+"/"+yVal+"/"+mVal+"/"+dVal);
            onValue(dbRef2,(snapshot2)=>{
                if(snapshot2.exists()){
                    AddItemToTable(childSnapshot.val().rollnumber,childSnapshot.val().username,snapshot2.val().Time,snapshot2.val().Date)
                }
            })
        });
    });
}
remove(ref(db,"Admin/"+userid+"/"+"qr"));
let myInterval;
document.getElementById('qrbtn').addEventListener('click',()=>{
    qrbtn.style.display = 'none';
    qrstopbtn.style.display = 'initial';
    qrsize = document.getElementById("qrsize").value;
    qrtime = document.getElementById("qrtime").value;
    document.querySelector(".output").style.display = 'inline-block';
    document.querySelector(".qroptions").style.display = "none";
    qrgenerator();
    myInterval = setInterval(hero,qrtime);
});
function hero(){
    let qrcodeimg = document.querySelector(".output");
    qrcodeimg.innerHTML="";
    setTimeout(qrgenerator,500);
}
function qrgenerator(){
    var result = userid+"/";
    var characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    var charactersLength = characters.length;
    for ( var i = 0; i < 20; i++ ) {
        result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    result+="/";
    
    result+=usernam;
    result+="/";
    result+=classval;
    console.log(result);
    let qrcodeimg = document.querySelector(".output");
    set(ref(db,"Admin/"+userid+"/"+"qr"),{
        qrdata: result
    });    
    qrcodeimg.innerHTML = `<img src="https://api.qrserver.com/v1/create-qr-code/?size=${qrsize}&data=${result}"></img>`;
}
document.getElementById('qrstopbtn').addEventListener('click',()=>{
    remove(ref(db,"Admin/"+userid+"/"+"qr"))
    clearInterval(myInterval);
    document.querySelector(".output").style.display = 'none';
    document.querySelector(".qroptions").style.display = "initial";
    qrbtn.style.display = 'initial';
    qrstopbtn.style.display = 'none';
});
document.getElementById("excel").addEventListener('click',function(){
    var table2excel = new Table2Excel();
    table2excel.export(document.getElementById("download-excel"));
})
const container = document.querySelector(".container"),
      pwShowHide = document.querySelectorAll(".showHidePw"),
      pwFields = document.querySelectorAll(".password"),
      signUp = document.querySelector(".signup-link"),
      login = document.querySelector(".login-link");

    //   js code to show/hide password and change icon
    pwShowHide.forEach(eyeIcon =>{
        eyeIcon.addEventListener("click", ()=>{
            pwFields.forEach(pwField =>{
                if(pwField.type ==="password"){
                    pwField.type = "text";

                    pwShowHide.forEach(icon =>{
                        icon.classList.replace("uil-eye-slash", "uil-eye");
                    })
                }else{
                    pwField.type = "password";

                    pwShowHide.forEach(icon =>{
                        icon.classList.replace("uil-eye", "uil-eye-slash");
                    })
                }
            }) 
        })
    })

    // js code to appear signup and login form
    window.addEventListener("contextmenu", e => e.preventDefault());

    signUp.addEventListener("click", ( )=>{
        container.classList.add("active");
    });
    login.addEventListener("click", ( )=>{
        container.classList.remove("active");
    });
    function my_onkeydown_handler( e ) {
        // if (event.ctrlKey && (event.keyCode === 85 || event.keyCode === 83 || event.keyCode ===65 )) {
            
        //     return false;
        //  }
        switch (event.keyCode) {
            case 116 : // 'F5'
                event.preventDefault();
                event.keyCode = 0;
                window.status = "F5 disabled";
                break;
            case 123 : // 'F12'
                event.preventDefault();
                event.keyCode = 0;
                window.status = "F12 disabled";
                break;
            case 21 : // 'CTRL+U'
                event.preventDefault();
                event.keyCode = 0;
                window.status = "Ctrl+U disabled";
                break;
        }
        if(e.ctrlKey && e.shiftKey && e.keyCode == 'I'.charCodeAt(0)){
            return false;
        }
        if(e.ctrlKey && e.shiftKey && e.keyCode == 'J'.charCodeAt(0)){
            return false;
        }
        if(e.ctrlKey && e.keyCode == 'U'.charCodeAt(0)){
            return false;
        }
        if(e.ctrlKey && e.keyCode == 'S'.charCodeAt(0)){
            return false;
        }
        if(e.ctrlKey && e.keyCode == 's'.charCodeAt(0)){
            return false;
        }
        if(e.ctrlKey && e.shiftKey && e.keyCode == 'C'.charCodeAt(0)){
            return false;
        }  
    }
    document.addEventListener("keydown", my_onkeydown_handler);
