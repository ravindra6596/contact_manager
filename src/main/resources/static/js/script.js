//alert("ok")
const toggleSidebar = () => {
  if ($(".sidebar").is(":visible")) {
    //true  
    //hide          
    console.log('IF')
    $(".sidebar").css("display", "none");
    $(".content").css("margin-left", "0%");
  } else {
    //false
    //show
    console.log('ELSE')
    $(".sidebar").css("display", "block");
    $(".content").css("margin-left", "20%");
  }
};

$("#success-alert").fadeTo(2000, 500).slideUp(500, function () {
  $("#success-alert").slideUp(500);
});

function deleteContact(cid) {
  swal({
    title: "Are you sure?",
    text: "You want to delete this contact !....",
    icon: "warning",
    buttons: true,
    dangerMode: true,
  })
    .then((willDelete) => {
      if (willDelete) {
        //swal("Poof! Your imaginary file has been deleted!", {
        //  icon: "success",
        // }); 
        window.location = "/user/delete/" + cid;
      } else {
        swal("Your Contact  is safe!");
      }
    });
}
const search = () => {
  // console.log("Call Search Function");
  let query = $("#search-input").val()
  if (query == '') {
    $(".search-result").hide();
  } else {
    //Serach Here
    // console.log(query);
    //send request to server
    let url = `http://localhost:8080/search/${query}`;//here we use backtick diplay keyboard tab butn above
    fetch(url).then(response => {
      return response.json();
    })
    .then((data) => {
      //data aceess
       //console.log(data)
      let text = `<div class='list-group'>`;
      data.forEach((contact)=>{
        text+=`<a href='/user/contact/${contact.cid}' class='list-group-item list-group-item-action'>${contact.name}</a>`;
      })
      text+=`</div>`;
      $(".search-result").html(text)
      $(".search-result").show();
    });
    
  }
 }
// $(document).ready(()=>{
//   $('.item').removeClass("active");
//   $('#home-link').addClass("active");
//    $('#profile-link').addClass("active");
//     $('#addcontact-link').addClass("active");
//      $('#viewcontact-link').addClass("active");
//       $('#setting-link').addClass("active");
// })