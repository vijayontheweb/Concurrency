module OpenSSL (
   getSHA1
  ,encodeBase64
  ,decodeBase64
  ,string2byteString) where
import qualified System.IO as SI
import qualified System.IO.Error as SIE
import qualified System.Process as SP
import qualified Control.Concurrent as CC
import qualified Data.ByteString as DB
import qualified Data.Word as DW
import qualified Data.Char as DC



openSSL args bs = do
      let g = do
            ( Just hin , Just hout, _, _) <-
                     SP.createProcess (SP.proc "openssl" args) { SP.std_in = SP.CreatePipe, SP.std_out = SP.CreatePipe }
            DB.hPutStr hin bs
            SI.hClose hin
            str <- SI.hGetLine hout
            return (Right str)
      SIE.catchIOError g (\e -> return (Left ("Error running openssl : " ++ (show e))))

getSHA1 :: DB.ByteString -> IO (Either String String)
getSHA1 = openSSL ["dgst", "-sha1"]           


encodeBase64 :: DB.ByteString -> IO (Either String String)
encodeBase64 = openSSL ["enc", "-e", "-a"]

decodeBase64 :: DB.ByteString -> IO (Either String String)
decodeBase64 = openSSL ["enc", "-d", "-a"]


string2byteString :: String -> DB.ByteString
string2byteString  str = DB.pack bytes
                         where bytes = map read ints :: [DW.Word8]
                               ints = map (show . DC.ord) str 

write :: String -> IO ()
write str = do
  (Right s) <- getSHA1 (string2byteString str)
  let byteString = DB.pack $ hex2bytes $ hexDigits $ take 40 $ drop 9 s      
  DB.writeFile "junk.txt" byteString
  putStrLn s
  where
    hexDigits [] = []
    hexDigits (a1:a2:as) = ('0':'x':a1:a2:[]) : hexDigits as
    hex2bytes xs = map read xs :: [DW.Word8]