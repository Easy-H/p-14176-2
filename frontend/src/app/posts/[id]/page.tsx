"use client"

import { useState, useEffect } from "react"
import { useParams, useRouter } from "next/navigation";
import { PostWithContentDto, PostCommentDto } from "@/type/post";
import { apiFetch } from "@/lib/backend/client";
import Link from "next/link";

export default function Page() {

    const router = useRouter();

    const { id: idStr } = useParams<{ id: string }>();
    const id =  Number(idStr);
    const [post, setPost] = useState<PostWithContentDto | null>(null);
    const [postComments, setPostComments] = useState<PostCommentDto[] | null>(null);

    useEffect(() => {
        apiFetch(`/api/v1/posts/${id}`)
            .then((data) => {
                setPost(data);
            });
        apiFetch(`/api/v1/posts/${id}/comments`)
            .then((data) => {
                setPostComments(data);
            })

    }, []);

    const deletePost = (id: number) => {
        apiFetch(`/api/v1/posts/${id}/delete?actorId=1`,
            {
                method: "DELETE"
            }
        ).then((data) => {
            alert(data.msg);
            router.replace("/posts");
        });
    }

    const deleteComment = (postId:number, commentId: number) => {
        apiFetch(`/api/v1/posts/${postId}/comments/${commentId}?actorId=3`,
            {
                method: "DELETE"
            }
        ).then((data) => {
            alert(data.msg);
            
            if (postComments == null) return;

            setPostComments(postComments?.filter((c) => c.id != commentId))
        });

    }

    const handleSubmitComment = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const form = e.target as HTMLFormElement;

        const contentTextarea = form.elements.namedItem(
            "content"
        ) as HTMLTextAreaElement;

        contentTextarea.value = contentTextarea.value.trim();

        if (contentTextarea.value.length === 0) {
            alert("내용을 입력해주세요.");
            contentTextarea.focus();
            return;
        }

        apiFetch(`/api/v1/posts/${id}/comments?actorId=3`, {
            method: "POST",
            body: JSON.stringify({
                content: contentTextarea.value
            })
        }).then((data) => {
            alert(data.msg);
            contentTextarea.value = "";
            if (postComments == null) {
                setPostComments([data.data]);
                return;
            }
            setPostComments([...postComments, data.data])
        })
    }

    if (post == null)
        return (<div>로딩중</div>);

    return (
        <div>
            <h1>글 상세페이지</h1>
            <div>번호: {post.id}</div>
            <h1>제목: {post.title}</h1>
            <div style={{ whiteSpace: 'pre-line' }}>
                {post.content}
            </div>
            <div className="flex gap-2">
                <button
                    className="p-2 rounded border"
                    onClick={() => {
                        confirm(`${post.id}번 글을 정말로 삭제하시겠습니까?`)
                            && deletePost(post.id)
                    }}
                >
                    삭제
                </button>
                <Link className="p-2 rounded border" href={`/posts/${post.id}/edit`}>
                    수정
                </Link>
            </div>
            <form className="flex flex-col gap-2 p-2" onSubmit={handleSubmitComment}>
                <textarea
                    className="border p-2 rounded"
                    name="content"
                    placeholder="내용"
                />
                <button className="border p-2 rounded" type="submit">
                    저장
                </button>
            </form>
            <h1>댓글 목록</h1>
            <ul>
                {postComments?.map((c) => {
                    return (<li key={c.id}>{c.content}
                        <button className="border p-2 rounded" onClick={()=>{
                                confirm(`${c.id}번 댓글을 정말로 삭제하시겠습니까?`) && deleteComment(id, c.id)
                            }}>삭제</button>
                        </li>)
                })}
            </ul>
        </div>
    );
}