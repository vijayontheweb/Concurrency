import qualified Network.Socket as NS
import qualified Control.Concurrent as CC
import qualified System.IO as SI
import qualified OpenSSL as OpenSSL
import qualified Codec.Compression.GZip as CCG
import qualified Data.ByteString.Lazy as DB



type HandlerFunc = SI.Handle -> IO ()

main = serveLog "9090" plainHandler

serveLog :: String              -- ^ Port number or name; 514 is default
         -> HandlerFunc         -- ^ Function to handle incoming messages
         -> IO ()
serveLog port handlerfunc = NS.withSocketsDo $
    do 
       addrinfos <- NS.getAddrInfo 
                    (Just (NS.defaultHints {NS.addrFlags = [NS.AI_PASSIVE]}))
                    Nothing (Just port)
       let serveraddr = head addrinfos

       sock <- NS.socket (NS.addrFamily serveraddr) NS.Stream NS.defaultProtocol
       NS.setSocketOption sock NS.ReuseAddr 1

       NS.bindSocket sock (NS.addrAddress serveraddr)

       NS.listen sock 5

       lock <- CC.newMVar ()

       procRequests lock sock

    where
          -- | Process incoming connection requests
          procRequests :: CC.MVar () -> NS.Socket -> IO ()
          procRequests lock mastersock = 
              do (connsock, clientaddr) <- NS.accept mastersock
                 CC.forkIO $ procMessages lock connsock clientaddr
                 procRequests lock mastersock

          -- | Process incoming messages
          procMessages :: CC.MVar () -> NS.Socket -> NS.SockAddr -> IO ()
          procMessages lock connsock clientaddr =
              do connhdl <- NS.socketToHandle connsock SI.ReadWriteMode
                 SI.hSetBuffering connhdl SI.LineBuffering
                 handlerfunc connhdl
                 
                 
-- A simple handler that prints incoming packets

readRawHeader :: SI.Handle -> IO [String]
readRawHeader h = do
  line' <- SI.hGetLine h
  let line = take (length line' -1) line'
  rest <- if length line > 0 then readRawHeader h else return []
  return (line:rest)

getHeaderValue :: String -> [String] -> String
getHeaderValue _ [] = []               
getHeaderValue h (s:ss) = if rightHeader then extractKey else getHeaderValue h ss where
  headerField = h ++ ": "
  lengthOfHeaderField = length headerField
  extractKey = drop lengthOfHeaderField s
  rightHeader = (take lengthOfHeaderField s) == headerField


getResponseHeaders :: String -> String -> [String]
getResponseHeaders protocol key =
  [
      "HTTP/1.1 101 Switching Protocols"
    , "Upgrade: websocket"
    , "Connection: Upgrade"
    , "Sec-WebSocket-Accept: " ++ key
    , ""
    --, "Sec-WebSocket-Protocol: " ++ protocol
    --, "Orgin: null"
  ]


plainHandler :: HandlerFunc
plainHandler h  = do
    hdr <- readRawHeader h
    putStrLn "REQUEST"
    foldr (>>) (return ())  $  map putStrLn $ map (\e -> e ++ "\r") hdr
    let key = getHeaderValue "Sec-WebSocket-Key" hdr
    putStrLn $ "key = " ++ (show key)
    let protocol = getHeaderValue "Sec-WebSocket-Protocol" hdr
    let totalKey = key ++ "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"

    putStrLn totalKey

--    let totalKey = "x3JJHMbDL1EzLkh9GBhXDw==258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
    (Right sha1) <- OpenSSL.getSHA1 (OpenSSL.string2byteString totalKey)
    (Right base64EncodedSHA1) <- OpenSSL.encodeBase64 sha1
    let respHdr = getResponseHeaders protocol base64EncodedSHA1
    foldr (>>) (return ())  $  map (SI.hPutStr h) $ map (\e -> e ++ "\r\n") respHdr
    foldr (>>) (return ())  $  map putStr $ map (\e -> e ++ "\r\n") respHdr
    SI.hFlush h
    SI.hSetBuffering h SI.NoBuffering
    talk h
    

talk h = do
    putStrLn "printing out hello world"
    let bs = DB.pack [0x81,4,66,66,66,10] 
    --let cbs = CCG.compress bs
    putStrLn (show bs)
    DB.hPutStr h bs
    --SI.hPutStrLn h "HELLOWORLD"
    SI.hFlush h
    CC.threadDelay 10000000
    talk h