import { useState, useRef, useEffect } from 'react'
import { motion, AnimatePresence } from 'framer-motion'
import { Sparkle, ArrowUp, X, ChatTeardropText, Spinner, Link as LinkIcon, Trash } from '@phosphor-icons/react'
import { useSubjectQa } from '../../hooks/useSubjectQa'

interface Message {
    id: string
    sender: 'student' | 'ai'
    content: string
    sources?: string[]
}

const SUGGESTIONS = [
    "Làm sao để học tốt môn Đại cương Giải tích tại UIT?",
    "Môn SE101 dạy về những chủ đề gì?",
    "Có đồ án mẫu nào của môn Phát triển ứng dụng Web không?",
    "Môn Cơ sở dữ liệu thi tự luận hay vấn đáp?"
]

export function AiChatWidget() {
    const [isOpen, setIsOpen] = useState(false)
    const [input, setInput] = useState('')
    const [sessionId, setSessionId] = useState<string | undefined>(() => {
        return localStorage.getItem('orbit_chat_session_id') || undefined
    })
    const [messages, setMessages] = useState<Message[]>(() => {
        const saved = localStorage.getItem('orbit_chat_messages')
        return saved ? JSON.parse(saved) : [
            {
                id: 'welcome',
                sender: 'ai',
                content: 'Chào bạn! Mình là Cố vấn Học tập AI của DevOrbit. Bạn cần hỏi điều gì về môn học, đề cương, cách tính điểm hay tham khảo đồ án mẫu UIT không?'
            }
        ]
    })

    const chatMutation = useSubjectQa()
    const messagesEndRef = useRef<HTMLDivElement>(null)

    // Save chat history to localStorage
    useEffect(() => {
        localStorage.setItem('orbit_chat_messages', JSON.stringify(messages))
        if (sessionId) {
            localStorage.setItem('orbit_chat_session_id', sessionId)
        }
    }, [messages, sessionId])

    // Scroll to bottom on new message
    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
    }, [messages, chatMutation.isPending])

    const handleSend = async (text: string) => {
        if (!text.trim()) return

        const userMsg: Message = {
            id: Date.now().toString(),
            sender: 'student',
            content: text
        }

        setMessages(prev => [...prev, userMsg])
        setInput('')

        try {
            const res = await chatMutation.mutateAsync({
                message: text,
                sessionId: sessionId
            })

            if (res.sessionId && res.sessionId !== sessionId) {
                setSessionId(res.sessionId)
            }

            const aiMsg: Message = {
                id: (Date.now() + 1).toString(),
                sender: 'ai',
                content: res.answer,
                sources: res.sources
            }

            setMessages(prev => [...prev, aiMsg])
        } catch (error) {
            // If server rejected sessionId (invalid UUID), reset it
            if (error instanceof Error && error.message.includes('400')) {
                setSessionId(undefined)
                localStorage.removeItem('orbit_chat_session_id')
            }
            const errorMsg: Message = {
                id: (Date.now() + 1).toString(),
                sender: 'ai',
                content: '⚠️ Đã xảy ra lỗi khi kết nối với trợ lý AI. Vui lòng thử lại sau.'
            }
            setMessages(prev => [...prev, errorMsg])
        }
    }

    const clearHistory = () => {
        setMessages([
            {
                id: 'welcome',
                sender: 'ai',
                content: 'Chào bạn! Mình là Cố vấn Học tập AI của DevOrbit. Bạn cần hỏi điều gì về môn học, đề cương, cách tính điểm hay tham khảo đồ án mẫu UIT không?'
            }
        ])
        setSessionId(undefined)
        localStorage.removeItem('orbit_chat_messages')
        localStorage.removeItem('orbit_chat_session_id')
    }

    // Helper to format course codes as clickable badges
    const renderMessageContent = (content: string) => {
        const parts = []
        const coursePattern = /\b([A-Z]{2,4}\d{3,4})\b/g
        let lastIndex = 0
        let match

        while ((match = coursePattern.exec(content)) !== null) {
            const index = match.index
            const code = match[1]

            if (index > lastIndex) {
                parts.push(content.substring(lastIndex, index))
            }

            parts.push(
                <button
                    key={index}
                    onClick={() => {
                        window.location.href = `/courses?search=${code}`
                    }}
                    className="inline-flex items-center mx-1 px-1.5 py-0.5 rounded bg-orbit-accent/15 text-orbit-accent font-bold hover:bg-orbit-accent/25 transition-[background-color] text-[13px] border border-orbit-accent/10 focus:outline-none focus:ring-1 focus:ring-orbit-accent"
                >
                    {code}
                </button>
            )
            lastIndex = coursePattern.lastIndex
        }

        if (lastIndex < content.length) {
            parts.push(content.substring(lastIndex))
        }

        // Return parsed nodes or plain text if no course codes found
        return parts.length > 0 ? parts : content
    }

    return (
        <div className="fixed bottom-6 right-6 z-50 flex flex-col items-end">
            {/* ─── CHATBOX ─── */}
            <AnimatePresence>
                {isOpen && (
                    <motion.div
                        initial={{ opacity: 0, scale: 0.95, y: 15 }}
                        animate={{ opacity: 1, scale: 1, y: 0 }}
                        exit={{ opacity: 0, scale: 0.95, y: 15 }}
                        transition={{ type: 'spring', stiffness: 350, damping: 30 }}
                        className="w-[90vw] sm:w-[420px] h-[600px] max-h-[80vh] mb-4 flex flex-col rounded-[2rem] border border-zinc-800/50 bg-zinc-950/80 backdrop-blur-xl shadow-[0_20px_50px_rgba(0,0,0,0.5)] overflow-hidden"
                    >
                        {/* Header */}
                        <div className="flex items-center justify-between px-6 py-4 border-b border-zinc-800/40 bg-zinc-900/10">
                            <div className="flex items-center gap-3">
                                <div className="h-9 w-9 rounded-xl bg-orbit-accent/15 border border-orbit-accent/20 flex items-center justify-center relative">
                                    <Sparkle className="h-5 w-5 text-orbit-accent animate-pulse" weight="fill" />
                                    <span className="absolute bottom-0 right-0 h-2 w-2 rounded-full bg-emerald-400 border border-zinc-950" />
                                </div>
                                <div>
                                    <h4 className="text-[14px] font-bold text-zinc-100">DevOrbit AI</h4>
                                    <p className="text-[11px] text-zinc-400">Trợ lý Cố vấn Học tập UIT</p>
                                </div>
                            </div>
                            <div className="flex items-center gap-2">
                                <button
                                    onClick={clearHistory}
                                    title="Xóa lịch sử chat"
                                    className="p-1.5 rounded-lg text-zinc-400 hover:text-rose-400 hover:bg-zinc-800/40 transition-colors"
                                >
                                    <Trash className="h-4.5 w-4.5" />
                                </button>
                                <button
                                    onClick={() => setIsOpen(false)}
                                    className="p-1.5 rounded-lg text-zinc-400 hover:text-zinc-100 hover:bg-zinc-800/40 transition-colors"
                                >
                                    <X className="h-5 w-5" />
                                </button>
                            </div>
                        </div>

                        {/* Messages Body */}
                        <div className="flex-1 overflow-y-auto p-6 space-y-4 scrollbar-thin">
                            {messages.map(msg => (
                                <div
                                    key={msg.id}
                                    className={`flex flex-col ${
                                        msg.sender === 'student' ? 'items-end' : 'items-start'
                                    }`}
                                >
                                    <div
                                        className={`max-w-[85%] px-4 py-3 rounded-2xl text-[14px] leading-relaxed ${
                                            msg.sender === 'student'
                                                ? 'bg-orbit-accent/10 border border-orbit-accent/20 text-orbit-accent rounded-tr-none'
                                                : 'bg-zinc-900/50 border border-zinc-850 text-zinc-100 rounded-tl-none'
                                        }`}
                                    >
                                        <div className="whitespace-pre-line">
                                            {typeof msg.content === 'string' ? renderMessageContent(msg.content) : msg.content}
                                        </div>
                                        
                                        {/* Reference links */}
                                        {msg.sources && msg.sources.length > 0 && (
                                            <div className="mt-3 pt-2.5 border-t border-zinc-800/50 flex flex-col gap-1.5">
                                                <span className="text-[10px] font-bold text-zinc-400 flex items-center gap-1">
                                                    <LinkIcon className="h-3 w-3" /> Nguồn tham khảo:
                                                </span>
                                                <div className="flex flex-wrap gap-1.5">
                                                    {msg.sources.map((src, i) => {
                                                        const label = src.includes("reddit.com") ? "Reddit" :
                                                                      src.includes("forum.uit") ? "UIT Forum" :
                                                                      src.includes("svuit.org") ? "SVUIT" :
                                                                      src.includes("giasuplus") ? "Giasuplus" : "Tài liệu";
                                                        return (
                                                            <a
                                                                key={i}
                                                                href={src}
                                                                target="_blank"
                                                                rel="noopener noreferrer"
                                                                className="inline-flex items-center gap-1 text-[10px] text-zinc-400 hover:text-orbit-accent bg-zinc-950 border border-zinc-850 px-2 py-1 rounded-lg transition-colors"
                                                            >
                                                                {label} ({i + 1})
                                                            </a>
                                                        )
                                                    })}
                                                </div>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            ))}

                            {chatMutation.isPending && (
                                <div className="flex items-center gap-2 text-[12px] text-zinc-400 bg-zinc-900/20 border border-zinc-900 px-4 py-2.5 rounded-2xl w-[100px]">
                                    <Spinner className="h-4 w-4 animate-spin text-orbit-accent" />
                                    <span>AI đang viết...</span>
                                </div>
                            )}
                            <div ref={messagesEndRef} />
                        </div>

                        {/* Suggestion Chips */}
                        {messages.length === 1 && (
                            <div className="px-6 py-2 flex flex-col gap-1.5">
                                <span className="text-[10px] font-bold text-zinc-400">Gợi ý câu hỏi:</span>
                                <div className="flex flex-wrap gap-1.5 max-h-[85px] overflow-y-auto">
                                    {SUGGESTIONS.map((s, i) => (
                                        <button
                                            key={i}
                                            onClick={() => handleSend(s)}
                                            className="text-[11px] text-left text-zinc-300 hover:text-orbit-accent bg-zinc-900/40 border border-zinc-850 hover:border-orbit-accent/30 rounded-xl px-3 py-1.5 transition-colors duration-200"
                                        >
                                            {s}
                                        </button>
                                    ))}
                                </div>
                            </div>
                        )}

                        {/* Message Input Box */}
                        <div className="p-4 border-t border-zinc-800/40 bg-zinc-900/10">
                            <form
                                onSubmit={(e) => {
                                    e.preventDefault()
                                    handleSend(input)
                                }}
                                className="relative flex items-center"
                            >
                                <input
                                    type="text"
                                    value={input}
                                    onChange={(e) => setInput(e.target.value)}
                                    placeholder="Hỏi về môn học, đề cương, cách ôn thi..."
                                    className="w-full bg-zinc-900 border border-zinc-800 hover:border-zinc-700 focus:border-orbit-accent/50 focus:ring-1 focus:ring-orbit-accent/50 rounded-2xl pl-4 pr-12 py-3 text-[14px] text-zinc-100 placeholder:text-zinc-500 transition-[border-color,box-shadow] outline-none"
                                />
                                <button
                                    type="submit"
                                    disabled={!input.trim() || chatMutation.isPending}
                                    className="absolute right-2.5 h-8 w-8 rounded-xl bg-orbit-accent hover:bg-orbit-accent/90 disabled:bg-zinc-800 text-zinc-950 disabled:text-zinc-500 flex items-center justify-center transition-all focus:outline-none"
                                >
                                    <ArrowUp className="h-4.5 w-4.5" weight="bold" />
                                </button>
                            </form>
                        </div>
                    </motion.div>
                )}
            </AnimatePresence>

            {/* ─── FLOATING TOGGLE BUTTON ─── */}
            <motion.button
                onClick={() => setIsOpen(!isOpen)}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                className={`h-14 w-14 rounded-full shadow-2xl flex items-center justify-center border transition-all duration-300 ${
                    isOpen
                        ? 'bg-zinc-900 border-zinc-800 text-zinc-100 hover:bg-zinc-800'
                        : 'bg-orbit-accent border-orbit-accent/20 text-zinc-950 hover:shadow-[0_0_20px_rgba(52,211,153,0.4)]'
                }`}
            >
                {isOpen ? (
                    <X className="h-6 w-6" weight="bold" />
                ) : (
                    <ChatTeardropText className="h-6 w-6" weight="fill" />
                )}
            </motion.button>
        </div>
    )
}
