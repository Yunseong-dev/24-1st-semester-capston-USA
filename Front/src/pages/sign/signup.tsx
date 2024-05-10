import { useState } from "react"
import { customAxios } from "../../utils/axios"
import { useNavigate } from 'react-router-dom'

const test = () => {
   const [name, setName] = useState("")
   const [phoneNumber, setPhone] = useState("")
   const [verNumber, setVerNumber] = useState("")
   const [password, setPassword] = useState("")

   const navigate = useNavigate()

   const isValidPhoneNumber = (phoneNumber: string) => {
      const regex = /^\d{11}$/
      return regex.test(phoneNumber)
   }

   const signup = async (e: { preventDefault: () => void }) => {
      e.preventDefault()

      try {
         if (!name || !phoneNumber || !verNumber || !password) {
            return alert('모든 입력란을 작성해주세요')
         }

         await customAxios.post('/api/user/signup', {
            name,
            phoneNumber,
            verNumber,
            password
         })

         navigate('/')
         alert("회원가입에 성공하였습니다")

      } catch (error: any) {
         if (error.response && error.response.data) {
            alert(error.response.data)
         }
      }
   }

   const verificationCode = async (e: { preventDefault: () => void }) => {
      e.preventDefault()

      try {
         if (!phoneNumber) {
            return alert("전화번호를 작성해주세요")
         }

         if (!isValidPhoneNumber(phoneNumber)) {
            return alert("올바른 전화번호 형식이 아닙니다")
         }

         await customAxios.post('/api/sms', {
            phoneNumber
         })

         alert("인증번호가 전송되었습니다")

      } catch (error: any) {
         if (error.response && error.response.data) {
            alert("인증번호 전송 과정 중 오류가 발생했습니다")
         }
      }
   }

   return (
      <div>
         <form onSubmit={signup}>
            <input type="text" value={name} onChange={(e) => setName(e.target.value)} placeholder="이름" />
            <input type="text" value={phoneNumber} onChange={(e) => setPhone(e.target.value)} placeholder="전화번호" />
            <input type="text" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="비밀번호" />
            <input type="text" value={verNumber} onChange={(e) => setVerNumber(e.target.value)} placeholder="인증번호" />
            <button onClick={verificationCode}>인증코드 전송</button>
            <button type="submit">회원가입</button>
         </form>
      </div>
   )
}

export default test
