

IMP.init("imp11827260");

//유저 정보
const user_email = "";

const today = new Date();
const hours = today.getHours();
const minutes = today.getMinutes();
const seconds = today.getSeconds();
const miliseconds = today.getMilliseconds();
const merchant_uid = hours +  minutes + seconds + miliseconds;

async function requestPay(orderData){
  return new Promise((resolve, reject) => {
    var courseName = orderData.orderedCourseRequests[0].courseName;
    const courseCount = orderData.orderedCourseRequests.length;
    if (courseCount > 1) {
      courseName += " 외 " + (courseCount - 1) + "개";
    }
    IMP.request_pay(
        {
          pg          : "html5_inicis",
          pay_method  : "card",
          merchant_uid: "HomeLearnTest" + new Date().getTime(),
          name        : courseName,
          amount      : orderData.orderPrice,
          buyer_id    : "", //사용자 ID넣어주면 될듯
          //buyer_name : $('#name').val(),
          //buyer_tel : "",
          //buyer_addr : "",
          //buyer_postcode : "",
          //m_redirect_url : "{모바일에서 결제 완료 후 리디렉션 될 URL}", //ex) https://www.xxx.com/
        },
        async (response) => {
          if (response.error_code != null) {
            return alert(`결제에 실패하였습니다. 에러 내용: ${response.error_msg}`);
          }
          if (response.success) {
            // 고객사 서버에서 /payment/complete 엔드포인트를 구현해야 합니다.
            const notified = await fetch("../payment/complete", {
              method : "POST",
              headers: {"Content-Type": "application/json"},
              // imp_uid와 merchant_uid, 주문 정보를 서버에 전달합니다
              body: JSON.stringify({
                imp_uid     : response.imp_uid,
                merchant_uid: response.merchant_uid,
                amount      : response.paid_amount,
                // 주문 정보...
              }),
            });
            const result = await notified.json();
            alert(result.message || "결제가 완료되었습니다");
            //alert("결제 성공");

            resolve(result);
          } else {
            alert("결제를 실패했습니다");
          }
        });
  });
}

async function cancelPay() {
  // 결제 취소를 위한 임시 merchant_uid와 imp_uid 설정
  const imp_uid = "imp_141005603992";
  const merchant_uid = "nobody_1724308011723";
  const cancel_amount =0; //환불 받을 금액 //0이나 null이면 전액 환불


  const notified = await fetch("../payment/cancel", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      imp_uid: imp_uid,
      merchant_uid: merchant_uid,
      amount: cancel_amount,
    }),
  });

  const result = await notified.json();
  alert(result.message || "결제 취소가 완료되었습니다");
}