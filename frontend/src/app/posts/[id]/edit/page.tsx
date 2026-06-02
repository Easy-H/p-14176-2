"use client"

import { apiFetch } from "@/lib/backend/client";
import { PostWithContentDto } from "@/type/post";
import { useParams, useRouter } from "next/navigation";
import { useEffect, useState } from "react";

export default function Page() {

    const { id: idStr } = useParams();
    const id = Number(idStr);
    const router = useRouter();
    const [post, setPost]= useState<PostWithContentDto | null>(null);

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const form = e.target as HTMLFormElement;

        const titleInput = form.elements.namedItem("title") as HTMLInputElement;
        const contentTextarea = form.elements.namedItem(
            "content"
        ) as HTMLTextAreaElement;

        titleInput.value = titleInput.value.trim();

        if (titleInput.value.length === 0) {
            alert("제목을 입력해주세요.");
            titleInput.focus();
            return;
        }

        contentTextarea.value = contentTextarea.value.trim();

        if (contentTextarea.value.length === 0) {
            alert("내용을 입력해주세요.");
            contentTextarea.focus();
            return;
        }

        apiFetch(`/api/v1/posts/${id}?actorId=1`, {
            method: "PUT",
            body: JSON.stringify({
                title: titleInput.value,
                content: contentTextarea.value
            })
        }).then((data) => {
            alert(data.msg);
            router.replace(`/posts/${data.data.id}`);
        })
    };

    useEffect(() => {
        apiFetch(`/api/v1/posts/${id}`)
            .then(setPost);
    }, [])

    if (!post) {
        return <div>로딩중</div>
    }

    return (
        <>
            <h1>{id}번 글 수정</h1>

            <form className="flex flex-col gap-2 p-2" onSubmit={handleSubmit}>
                <input
                    className="border p-2 rounded"
                    type="text"
                    name="title"
                    placeholder="제목"
                    defaultValue={post.title}
                    autoFocus
                />
                <textarea
                    className="border p-2 rounded"
                    name="content"
                    defaultValue={post.content}
                    placeholder="내용"
                />
                <button className="border p-2 rounded" type="submit">
                    수정
                </button>
            </form>
        </>
    );
}